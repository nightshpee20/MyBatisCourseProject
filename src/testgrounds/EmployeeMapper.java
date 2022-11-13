package testgrounds;

import java.util.List;

public interface EmployeeMapper {
	public Employee selectEmployeeById(int id);
	
	public List<Employee> selectEmployeesByJobId(int id);
	
	public int insertEmployee(Employee emp);
	
	public int updateEmployee(Employee emp);
	
	public int deleteEmployeeById(int id);
}
