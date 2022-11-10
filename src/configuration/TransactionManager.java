package configuration;

import java.util.List;
import java.util.Map;

public class TransactionManager {
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
