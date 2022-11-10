package configuration;

public class Environment {
	public String id;
	public TransactionManager transactionManager;
	public DataSource dataSource;
	
	@Override
	public String toString() {
		return String.format("%s %s %s", id, transactionManager.toString(), dataSource.toString());
	}
}
