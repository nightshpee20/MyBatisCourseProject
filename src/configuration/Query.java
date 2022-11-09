package configuration;

public class Query {
	public String parameterType;
	public String resultType;
	public String resultMap;
	String sql;
	
	public void setSql(String sql) {
		if (sql != null)
			throw new IllegalStateException("SQL has already been added!");
//		TODO: Sql should be parsed to prepared statement syntax (#{id} => ?)	
	}
	
	public String getSql() {
		return sql;
	}
}
