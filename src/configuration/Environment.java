package configuration;

public class Environment {
	public TransactionManager transactionManager;
	public DataSource dataSource;
	
	@Override
	public String toString() {
		return transactionManager.toString() + " " + dataSource.toString();
	}
}
