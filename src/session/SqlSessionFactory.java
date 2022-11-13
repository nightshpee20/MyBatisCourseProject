package session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import configuration.Configuration;
import configuration.Mapper;
import connection.ConnectionPool;

public class SqlSessionFactory {
	Configuration config;
	Map<String, String> properties;
	ConnectionPool cp;
	
	SqlSessionFactory(Map<String, String> properties) throws ClassNotFoundException, SQLException, IOException {
		cp = ConnectionPool.getConnectionPool(properties);
	}
	
	public SqlSession openSession() {
		return new SqlSession(cp.getConnection(), config.mappers);
	}

	
	Configuration getConfiguration() {
		return config;
	}
}
