package session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import configuration.Configuration;
import connection.ConnectionPool;

public class SqlSessionFactory {
	Map<String, String> properties;
	ConnectionPool cp;
	
	SqlSessionFactory(Map<String, String> properties) throws ClassNotFoundException, SQLException, IOException {
		cp = ConnectionPool.getConnectionPool(properties);
	}
	
	public SqlSession openSession() {
		
	}
	
	Configuration getConfiguration() {
		return config;
	}
}
