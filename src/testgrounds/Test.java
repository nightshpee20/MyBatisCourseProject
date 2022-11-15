package testgrounds;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
			
//		File configFile = new File("C:\\Users\\night\\Desktop\\Java Internship\\MyBatis\\mybatis-config.xml");
//		Configuration config = XMLParser.getConfiguration(configFile);
//		
////		System.out.println(config.toString());
//		
//		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config, "development");
//		SqlSession sesh = factory.openSession();
////		
//		EmployeeMapper em = sesh.getMapper(EmployeeMapper.class);
//		Employee emp = em.selectEmployeeById(208);
//		System.out.println(emp.first_name);
		
//		List<Employee> emps = em.selectEmployeesByJobId(4);
//		for (Employee emp : emps)
//			System.out.println(emp.first_name);
		
		Employee newEmp = new Employee();
		newEmp.employee_id = 225;
		newEmp.first_name = "dddd";
		newEmp.last_name = "dddd";
		newEmp.email = "cccc@fake.com";
		newEmp.phone_number = "8673645901";
		newEmp.hire_date = java.sql.Date.valueOf(LocalDate.now());
		newEmp.salary = 10_000;
		newEmp.job_id = 4;
		newEmp.manager_id = 208;
		newEmp.department_id = 9;
		
//		int affectedRows = em.insertEmployee(newEmp);
//		System.out.println(affectedRows);
		
//		int affectedRows = em.updateEmployee(newEmp);
//		System.out.println(affectedRows);
		
//		int affectedRows = em.deleteEmployeeById(224);
//		System.out.println(affectedRows);
		
		//TEST insert()
//		int affectedRows = sesh.insert("INSERT INTO employees(first_name, last_name, email, phone_number, hire_date, job_id, salary, manager_id, department_id) VALUES(#{first_name},#{last_name},#{email},#{phone_number},#{hire_date},#{job_id},#{salary},#{manager_id},#{department_id})", newEmp);
//		System.out.println(affectedRows);
		
		//TEST update()
//		int affectedRows = sesh.update("UPDATE employees SET first_name = #{first_name}, last_name = #{last_name} WHERE employee_id = #{employee_id}", newEmp);
//		System.out.println(affectedRows);
		
		//TEST delete()
//		int empId = 225;
//		int affectedRows = sesh.delete("DELETE FROM employees WHERE employee_id = #{value}", 225);
//		System.out.println(affectedRows);
		
		String bruh = "";
		Integer num = null;
		try {
			num = Integer.parseInt(bruh);			
		} catch (NumberFormatException e) {
			num = 12;
		}
		System.out.println(num);
	}

}
