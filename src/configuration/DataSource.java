package configuration;

import java.util.List;

public class DataSource {
	public String type;
	public List<Property> properties;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		
		if (properties != null)
			for (Property prop : properties) {
				sb.append(" ");
				sb.append(prop.toString());
			}
		
		return sb.toString();
	}
}
