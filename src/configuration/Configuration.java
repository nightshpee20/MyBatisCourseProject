package configuration;

import java.util.List;

public class Configuration {
	public Environments environments;
	public List<Mapper> mappers;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(environments.toString());
		
		for (Mapper map : mappers) {
			sb.append(" ");
			sb.append(map.toString());
		}
		
		return sb.toString();
	}
}
