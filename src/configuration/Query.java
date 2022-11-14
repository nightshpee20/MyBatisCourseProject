package configuration;

public class Query {	
	public static final String PARAM_PAT = "#\\{[0-9a-zA-Z$_]*}";
	
	public String id;
	public String queryType;
	public String parameterType;
	public String resultType;
	public String resultMap;
	public String sql;
	
	@Override
	public String toString() {
		return String.format("{ID:%s QueryType:%s ParameterType:%s ResultType:%s ResultMap:%s SQL:%s}", id, queryType, parameterType, resultType, resultMap, sql);
	}
}
