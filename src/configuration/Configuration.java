package configuration;

//import java.util.List;
import java.util.Map;

public class Configuration {
	public Environments environments;
	public Map<String, Mapper> mappers;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(environments.toString());
		
//		for (Mapper map : mappers) {
//			sb.append(" ");
//			sb.append(map.toString());
//		}
		
		mappers.forEach((namespace, mapper) -> {
			sb.append(" ");
			sb.append(mapper.toString());
		});
		
		return sb.toString();
	}
}
