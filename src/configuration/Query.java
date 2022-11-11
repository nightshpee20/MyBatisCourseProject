package configuration;

public class Query {	
	public static final String PARAM_PAT = "#\\{[0-9a-zA-Z$_]*}";

	public String parameterType;
	public String resultType;
	public String resultMap;
	String sql;
	
	@Override
	public String toString() {
		return String.format("{ParameterType:%s ResultType:%s ResultMap:%s SQL:%s}", parameterType, resultType, resultMap, sql);
	}
	
	public void addSql(String sql) {
		this.sql = sql.replaceAll(PARAM_PAT, "?");
	}
}
