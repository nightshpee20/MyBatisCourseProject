package session;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import configuration.Configuration;

public class Test {

	public static void main(String[] args) throws ParserConfigurationException, URISyntaxException {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		Document document = null;
//		File test = new File("C:\\Users\\night\\Desktop\\test.xml");
//		try {
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			document = builder.parse(test);
//		} catch (SAXException | IOException e) {
//			e.printStackTrace();
//		}
//		
//		NodeList gruh = document.getElementsByTagName("gruh");
//		Element el = (Element)gruh.item(1);
//		
//		NodeList hruh = el.getElementsByTagName("sruh");
//		for (int i = 0; i < hruh.getLength(); i++) {
//			Element s = (Element)hruh.item(i);
//			System.out.println(s.getTextContent());
//		}
//		String type = el.getAttribute("type");
//		System.out.println(type.length());
			
		File configFile = new File("C:\\Users\\night\\Desktop\\Java Internship\\MyBatis\\mybatis-config.xml");
		Configuration config = utilities.XMLParser.getConfiguration(configFile);
		
		System.out.println(config.toString());

	}

}
