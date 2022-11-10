package configuration;

import java.util.List;

public class Environments {
	public String defaultt;
	public List<Environment> environments;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(defaultt);
		
		for (Environment prop : environments) {
			sb.append(" ");
			sb.append(prop.toString());
		}
		
		return sb.toString();
	}
}
