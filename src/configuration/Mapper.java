package configuration;

import java.util.Map;

public class Mapper {
	public String namespace;
	public Map<String, Query> queries;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(namespace);
		
		System.out.println(queries.size());
		queries.forEach((id, query) -> {
			sb.append(" ");
			sb.append(query);
		});
		
		return sb.toString();
	}
}
