package utilities;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import configuration.Configuration;
import configuration.Mapper;
import configuration.Query;

public class XMLParser {
	static Document document;
	static Configuration configuration;
	
	public static Configuration getConfiguration(File config) throws ParserConfigurationException, URISyntaxException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(config);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		configuration = new Configuration();
		
		getProperties(document, configuration);
		getMappers(document, configuration);
		
		return configuration;
	}

//	private static void getEnvironments();
	
	private static void getProperties() throws ParserConfigurationException {
		NodeList properties = document.getElementsByTagName("property");
		if (properties.getLength() == 0)
			throw new ParserConfigurationException("Missing property elements!");
		
		for (int i = 0; i < properties.getLength(); i++) {
			Node property = properties.item(i);
			
			if (property.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) property;
				String name = element.getAttribute("name");
				String val = element.getAttribute("value");
				
				if (name.length() == 0 || val.length() == 0)
					throw new ParserConfigurationException("Missing property name or value!");
				
				configuration.properties.put("password", val);
			}
		}
	}
	
	private static void getMappers() throws ParserConfigurationException, URISyntaxException {
		NodeList mappers = document.getElementsByTagName("mapper");
		
		for (int i = 0; i < mappers.getLength(); i++) {
			Node property = mappers.item(i);
			
			if (property.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) property;
				
				String value = element.getAttribute("resource");
				String type = "";
				if (value.length() != 0)
					type = "resource";
				if (type.length() == 0) {
					value = element.getAttribute("class");
					type = "class";
				}
				if (type.length() == 0)
					throw new ParserConfigurationException("Invalid mapper element!");
				System.out.println(value);
				System.out.println(type);
				switch (type) {
					case "resource":
						URL mapperUrl = XMLParser.class.getResource(value);
						Path mapperPath = Paths.get(mapperUrl.toURI());
						File mapperXML = mapperPath.toFile();
						parseMapper(mapperXML);
						break;
					case "class":
						break;
					case "name":
				}
			}
		}
	}
	
	private static Configuration parseMapper(File mapper) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(mapper);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		Mapper newMapper = new Mapper();
		
		Node mapperTag = document.getElementsByTagName("mapper").item(0);
		if (mapperTag == null)
			throw new ParserConfigurationException("Missing root tag (<mapper>) in " + mapper.getName());
		
		Element mapperEl = (Element)mapperTag;
		String namespace = mapperEl.getAttribute("namespace");
		if (namespace.length() == 0)
			throw new ParserConfigurationException("Missing namespace attribute in root tag in " + mapper.getName());
		
		newMapper.namespace = namespace;
		String[] queryTags = {"select", "insert", "update", "delete"};
		for (String queryTag : queryTags) 
			getQueries(document, newMapper, queryTag);
		
		return configuration;
	}

	private static void getQueries(Document document, Mapper newMapper, String queryTag)
			throws ParserConfigurationException {
		NodeList queriesInXML = document.getElementsByTagName(queryTag);
		Query query = null;
		for (int i = 0; i < queriesInXML.getLength(); i++) {
			Node queryNode = queriesInXML.item(i);
			
			if (queryNode.getNodeType() == Node.ELEMENT_NODE) {
				query = new Query();
				Element queryEl = (Element) queryNode;
				String methodId = queryEl.getAttribute("id");
				String resultType = queryEl.getAttribute("resultType");
				String parameterType = queryEl.getAttribute("parameterType");
				String sql = queryEl.getTextContent();
				
				if (methodId.length() == 0 || parameterType.length() == 0 || sql.length() == 0)
					throw new ParserConfigurationException("Missing query id/parameterType/sql!");
				
				query.resultType = resultType;
				query.parameterType = parameterType;
				query.setSql(sql);
				
				newMapper.queries.put(methodId, query);
//				System.out.println(methodId + " " + resultType + " " + parameterType + "\n" + sql);
			}
		}
	}
}
