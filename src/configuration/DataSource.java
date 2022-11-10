package configuration;

import java.util.Map;

public class DataSource {
	public String type;
	public Map<String, String> properties;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		
		if (properties != null)
			properties.forEach((name, value) -> {
				sb.append(" ");
				sb.append(name);
				sb.append(" ");
				sb.append(value);
			});
		
		return sb.toString();
	}
}
