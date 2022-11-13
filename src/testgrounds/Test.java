package testgrounds;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import configuration.Configuration;
import session.SqlSession;
import session.SqlSessionFactory;
import session.SqlSessionFactoryBuilder;
import utilities.XMLParser;

public class Test {
	public static void main(String[] args) throws Exception {
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
		Configuration config = XMLParser.getConfiguration(configFile);
		
//		System.out.println(config.toString());
		
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config, "development");
		SqlSession sesh = factory.openSession();
//		
		EmployeeMapper em = sesh.getMapper(EmployeeMapper.class);
		em.deleteEmployeeById(0);
	}

}
