package session;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.regex.Matcher;

import configuration.Query;

public class SqlSession {
	Connection con;
	ArrayList<Query> selects;
	ArrayList<Query> inserts;
	ArrayList<Query> updates;
	ArrayList<Query> deletes;
	
	SqlSession(Connection con) {
		this.con = con;
		selects = new ArrayList<>();
		inserts = new ArrayList<>();
		updates = new ArrayList<>();
		deletes = new ArrayList<>();
	}
	
	public void close() throws SQLException {
		con.close();
	}
	
//	public void commit() {
//		for (Query q : inserts) {
//			
//		}
//	}
	
	public <T> T selectOne(String statement, Object parameter) {
		statement = formatSql(statement);
		return selectObject(statement, Object.class, parameter);
	}
	
	public int insert(String statement, Object parameter) {
		return 0;
	}
	
	public void rollback() {
		inserts = new ArrayList<>();
		updates = new ArrayList<>();
		deletes = new ArrayList<>();
	}
	
	private <T> T selectObject(String sql, Class<T> c, Object params) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Statement query = con.createStatement();
		ResultSet rs = query.executeQuery(sql);
		rs.next();

		Constructor<T> constructor = c.getDeclaredConstructor();
		T res = (T)constructor.newInstance();

		populateResult(c, rs, res);

		return res;
	}

	private <T> void populateResult(Class<T> c, ResultSet rs, T res) throws IllegalAccessException, SQLException {
		Field[] fields = c.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
			Class fcl = f.getType();
			String fieldName = f.getName();
			
			if (fcl == Boolean.class || fcl == boolean.class) {
				f.set(res, rs.getBoolean(fieldName));
			} else if (fcl == Byte.class || fcl == byte.class) {
				f.set(res, rs.getByte(fieldName));
			} else if (fcl == Date.class) {
				f.set(res, rs.getDate(fieldName));
			} else if (fcl == Double.class || fcl == double.class) {
				f.set(res, rs.getDouble(fieldName));
			} else if (fcl == Float.class || fcl == float.class) {
				f.set(res, rs.getFloat(fieldName));
			} else if (fcl == Integer.class || fcl == int.class) {
				f.set(res, rs.getInt(fieldName));
			} else if (fcl == Long.class || fcl == long.class) {
				f.set(res, rs.getLong(fieldName));
			} else if (fcl == Short.class || fcl == short.class) {
				f.set(res, rs.getShort(fieldName));
			} else if (fcl == String.class) {
				f.set(res, rs.getString(fieldName));
			} else if (fcl == Time.class) {
				f.set(res, rs.getTime(fieldName));
			}
		}
	}
	
	private String formatSql(String sql) {
		return sql.replaceAll(Query.PARAM_PAT, "?");
	}
}
