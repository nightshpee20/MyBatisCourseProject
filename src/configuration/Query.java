package configuration;

public class Query {
	public String parameterType;
	public String resultType;
	public String resultMap;
	public String sql;
	
	@Override
	public String toString() {
		return String.format("{%s %s %s %s}", parameterType, resultType, resultMap, sql);
	}
}
