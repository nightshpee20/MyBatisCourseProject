package connection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import utilities.CircularBuffer;


public class ConnectionPool {
	class ConnectionTracker {
		Connection con;
		Instant lastUsed;

		public ConnectionTracker(Connection con) {
			this.con = con;
			lastUsed = Instant.now();
		}
	}
	
	class ConnectionHandler implements InvocationHandler {
		private Connection con;
		
		public ConnectionHandler(Connection con) {
			this.con = con;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (methodName.equals("close")) {
				releaseConnection(con);
				return null;
			}
			
			if (methodName.equals("prepareCall") || methodName.equals("prepareStatement")) {
				String sql = args[0].toString();
				LOGGER.info("SQL: " + sql);
			}
			
			return method.invoke(con, args);
		}
	}
	
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final int MIN_SIZE = 4;
	private static final int CONNECTIONS_COUNT = 16;
	private static final int REFRESH_PERIOD_MILIS = 1000 * 60 * 2;
	private static final int CONNECTION_RETURN_DEADLINE = 1000 * 60 * 20;
	private static final int CONNECTION_STANDBY_LIMIT = 1000 * 60 * 20;
	
	private static ConnectionPool instance;
	
	private CircularBuffer<ConnectionTracker> connectionPool;
	private ArrayList<ConnectionTracker> lentConnections;
	
	private ConnectionPool(Map<String, String> properties) throws SQLException, IOException, ClassNotFoundException {
		connectionPool = new CircularBuffer<>(CONNECTIONS_COUNT);
		lentConnections = new ArrayList<>();
		
		for (int i = 0 ; i < CONNECTIONS_COUNT; i++) {
			ConnectionTracker ct = new ConnectionTracker(createConnection(properties));
			connectionPool.add(ct);
		}
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
		    public void run() { 
		    	try {
		    		for (ConnectionTracker ct : connectionPool) {
		    			if (activityChecker(ct) && connectionPool.size > MIN_SIZE) {
		    				connectionPool.poll();
		    				ct.con.close();
		    			}
		    		}
		    		for (ConnectionTracker ct : lentConnections)
		    			returnChecker(ct);

		    	} catch (SQLException e) {
		    		e.printStackTrace();
		    	}
		    }
		}, REFRESH_PERIOD_MILIS, REFRESH_PERIOD_MILIS);
	}
	
	public static ConnectionPool getConnectionPool(Map<String, String> properties) throws SQLException, IOException, ClassNotFoundException {
		if (instance == null)
			instance = new ConnectionPool(properties);
		return instance;
	}
	
	private boolean activityChecker(ConnectionTracker ct) throws SQLException {
		long passedMin = Duration.between(ct.lastUsed, Instant.now()).toMinutes();
		if (passedMin > CONNECTION_STANDBY_LIMIT) {
			ct.con.close();
			return true;
		}
		
		return false;
	}
	
	private boolean returnChecker(ConnectionTracker ct) throws SQLException {
		long passedMin = Duration.between(ct.lastUsed, Instant.now()).toMinutes();
		if (passedMin > CONNECTION_RETURN_DEADLINE) 
			throw new IllegalStateException("A connection's return deadline has been missed!");
		return false;
	}
	
	private Connection createConnection(Map<String, String> properties) throws SQLException, IOException, ClassNotFoundException {
		String driver = properties.get("driver");
		String url = properties.get("url");
		String username = properties.get("username");
		String password = properties.get("password");
		
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url, username, password);
		
		var handler = new ConnectionHandler(con);
		Connection proxy = (Connection)Proxy.newProxyInstance(
			ClassLoader.getSystemClassLoader(),
			new Class[] {Connection.class},
			handler
		);
		
		return proxy;
	}
	
	public Connection getConnection() {
		if (connectionPool.size == 0)
			throw new IllegalStateException("There are no available connections!");
		ConnectionTracker ct = connectionPool.poll();
		lentConnections.add(ct);
		return ct.con;
	}
	
	private void releaseConnection(Connection con) {
		for (ConnectionTracker ct : lentConnections) {
			if (ct.con.equals(con)) {
				lentConnections.remove(ct);
				break;
			}
		}
		connectionPool.add(new ConnectionTracker(con));
	}
}