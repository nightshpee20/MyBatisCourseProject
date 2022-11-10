package configuration;

import java.util.Map;

public class Environments {
	public String defaultt;
	public Map<String, Environment> environments;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(defaultt);
		
		environments.forEach((id, environment) -> {
			sb.append(" ");
			sb.append(id);
			sb.append(" ");
			sb.append(environment.toString());
		});
		
		return sb.toString();
	}
}
