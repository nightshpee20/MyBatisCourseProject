package utilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import configuration.Configuration;
import configuration.DataSource;
import configuration.Environment;
import configuration.Environments;
import configuration.Mapper;
import configuration.Query;
import configuration.TransactionManager;

public class XMLParser {
	static Document document;
	static File configFile;
	
	private XMLParser() {}
	
	public static Configuration getConfiguration(File config) throws ParserConfigurationException, URISyntaxException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(config);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		if (document.getElementsByTagName("configuration").getLength() == 0)
			throw new ParserConfigurationException("Missing root tag <configuration> in " + configFile.getName());
		
		configFile = config;
		Configuration configuration = new Configuration();
		
		configuration.environments = getEnvironments();
		configuration.mappers = getMappers();
		
		return configuration;
	}

	private static Environments getEnvironments() throws ParserConfigurationException {
		NodeList environmentsNL = document.getElementsByTagName("environments");
		
		if (environmentsNL.getLength() == 0)
			return null;
		
		Environments environments = new Environments();
		Element environmentsEl = (Element)environmentsNL.item(0);
		environments.defaultt = environmentsEl.getAttribute("default");
		environments.environments = getEnvironmentTagsAsMap();
		
		return environments;
	};
	
	private static Map<String, Environment> getEnvironmentTagsAsMap() throws ParserConfigurationException {
		NodeList environmentNL = document.getElementsByTagName("environment");
		int len = environmentNL.getLength();
		
		if (len == 0)
			throw new ParserConfigurationException("No <environment> elements have been found in " + configFile.getName());
		
		Map<String, Environment> environmentM = new HashMap<>(len);
		
		for (int i = 0; i < len; i++) {
			Element environmentEl = (Element) environmentNL.item(i);
			
			String id = environmentEl.getAttribute("id");
			TransactionManager transactionManager = getTransactionManager(environmentEl);
			DataSource dataSource = getDataSource(environmentEl);
			
			if (id == null || transactionManager == null || dataSource == null)
				throw new ParserConfigurationException("Missing \'id/transactionManager/dataSource\' of <environment> somewhere in " + configFile.getName());
		
			Environment environment = new Environment();
			environment.transactionManager = transactionManager;
			environment.dataSource = dataSource;
			
			environmentM.put(id, environment);
		}
		
		return environmentM;
	}
	
	private static TransactionManager getTransactionManager(Element parrent) throws ParserConfigurationException {
		NodeList transactionManagerNL = parrent.getElementsByTagName("transactionManager");
		
		if (transactionManagerNL.getLength() == 0)
			throw new ParserConfigurationException("Missing <transactionManager> somewhere in " + configFile.getName());
		
		Element transactionManagerEl = (Element)transactionManagerNL.item(0);
		String type = transactionManagerEl.getAttribute("type");
		if (type.length() == 0)
			throw new ParserConfigurationException("Missing \'type\' of <transactionManager> somewhere in " + configFile.getName());
		
		Map<String, String> properties = getProperties(transactionManagerEl);
		
		TransactionManager transactionManager = new TransactionManager();
		transactionManager.type = type;
		transactionManager.properties = properties;
		
		return transactionManager;
	}
	//TODO: This method is basically the same as the one above - merge them
	private static DataSource getDataSource(Element parrent) throws ParserConfigurationException {
		NodeList dataSourceNL = parrent.getElementsByTagName("dataSource");
		if (dataSourceNL.getLength() == 0)
			throw new ParserConfigurationException("Missing <dataSource> somewhere in " + configFile.getName());
		
		Element dataSourceEl = (Element)dataSourceNL.item(0);
		String type = dataSourceEl.getAttribute("type");
		if (type.length() == 0)
			throw new ParserConfigurationException("Missing \'type\' of <dataSource> somewhere in " + configFile.getName());
		
		Map<String, String> properties = getProperties(dataSourceEl);
		
		DataSource dataSource = new DataSource();
		dataSource.type = type;
		dataSource.properties = properties;
		
		return dataSource;
	}
	
	private static Map<String, String> getProperties(Element parrent) throws ParserConfigurationException {
		NodeList propertyNL = parrent.getElementsByTagName("property");
		Map<String, String> propertiesM = new HashMap<>();
		
		for (int i = 0; i < propertyNL.getLength(); i++) {
			Element propertyEl = (Element)propertyNL.item(i);

			String name = propertyEl.getAttribute("name");
			String value = propertyEl.getAttribute("value");
			
			if (name.length() == 0 || value.length() ==0)
				throw new ParserConfigurationException("Missing \'name/value\' of <property> somewhere in " + configFile.getName());
			
			propertiesM.put(name, value);
		}
		
		return propertiesM.size() == 0 ? null : propertiesM;
	}
	
	private static Map<String, Mapper> getMappers() throws ParserConfigurationException, URISyntaxException {
		NodeList mappersNL = document.getElementsByTagName("mapper");
		Map<String, Mapper> mappersHM = new HashMap<>();
		
		for (int i = 0; i < mappersNL.getLength(); i++) {
			Element mapperEl = (Element)mappersNL.item(i);

			String value = mapperEl.getAttribute("resource");
			String type = "";
			if (value.length() != 0)
				type = "resource";
			if (type.length() == 0) {
				value = mapperEl.getAttribute("class");
				type = "class";
			}
			if (type.length() == 0)
				throw new ParserConfigurationException("Invalid mapper element!");
			
			switch (type) {
				case "resource":
					Path configPath = Paths.get(configFile.getAbsolutePath());
					Path mapperPath = configPath.resolveSibling(value);
					File mapper = mapperPath.toFile();
					Mapper newMap = parseMapper(mapper);
					String namespace = newMap.namespace;
					mappersHM.put(namespace, newMap);
					break;
				case "class":
					break;
			}
		}
		
		return mappersHM;
	}
	
	private static Mapper parseMapper(File mapper) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document mapperDocument = null;
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			mapperDocument = builder.parse(mapper);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		Node mapperTag = mapperDocument.getElementsByTagName("mapper").item(0);
		if (mapperTag == null)
			throw new ParserConfigurationException("Missing root tag (<mapper>) in " + mapper.getName());
		
		Element mapperEl = (Element)mapperTag;
		String namespace = mapperEl.getAttribute("namespace");
		if (namespace.length() == 0)
			throw new ParserConfigurationException("Missing namespace attribute in root tag in " + mapper.getName());
		
		Mapper newMapper = new Mapper();
		newMapper.namespace = namespace;
		newMapper.queries = new HashMap<>();
		
		String[] queryTags = {"select", "insert", "update", "delete"};
		for (String queryTag : queryTags) 	
			getQueries(mapperDocument, newMapper, queryTag);
		
		return newMapper;
	}

	private static void getQueries(Document document, Mapper newMapper, String queryTag) throws ParserConfigurationException {
		NodeList queriesInXML = document.getElementsByTagName(queryTag);
		Query query = null;

		for (int i = 0; i < queriesInXML.getLength(); i++) {
			Element queryEl = (Element)queriesInXML.item(i);
			
			String methodId = queryEl.getAttribute("id");
			String queryType = queryEl.getTagName(); 
			String resultType = queryEl.getAttribute("resultType");
			String parameterType = queryEl.getAttribute("parameterType");
			String sql = queryEl.getTextContent().strip();

			if (methodId.length() == 0 || parameterType.length() == 0 || sql.length() == 0)
				throw new ParserConfigurationException("Missing query id/parameterType/sql!");

			query = new Query();
			query.id = methodId;
			query.queryType = queryType;
			query.resultType = resultType;
			query.parameterType = parameterType;
			query.sql = sql;
			newMapper.queries.put(methodId, query);
		}
	}
}
