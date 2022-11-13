package testgrounds;

import java.sql.Date;

public class Employee {
	public int employee_id;
	public String first_name;
	public String last_name;
	public String email;
	public String phone_number;
	public Date hire_date;
	public int job_id;
	public double salary;
	public int manager_id;
	public int department_id;
	
	public Employee() {}
	
	public Employee(int employee_id, String first_name, String last_name, int job_id) {
		this.employee_id = employee_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.job_id = job_id;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(employee_id);
		sb.append('\n');
		sb.append(first_name);
		sb.append('\n');
		sb.append(last_name);
		sb.append('\n');
		sb.append(email);
		sb.append('\n');
		sb.append(phone_number);
		sb.append('\n');
		sb.append(hire_date);
		sb.append('\n');
		sb.append(job_id);
		sb.append('\n');
		sb.append(salary);
		sb.append('\n');
		sb.append(manager_id);
		sb.append('\n');
		sb.append(department_id);
		sb.append('\n');
		
		return sb.toString();
	}
}