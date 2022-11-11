package session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import configuration.Configuration;
import configuration.Environment;

public class SqlSessionFactoryBuilder {
	Configuration config;
	String environment;
	
	public SqlSessionFactory build(Configuration config, String environment) throws ClassNotFoundException, SQLException, IOException {
		this.config = config;
		this.environment = environment;
		
		Map<String, String> properties = getProperties();
		SqlSessionFactory ssf = new SqlSessionFactory(properties);
		ssf.config = config;
		
		return ssf;
	}
	
	private Map<String, String> getProperties() {
		Environment env = config.environments.environments.get(environment);
		return env.dataSource.properties;
	}
}
