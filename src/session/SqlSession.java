package session;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import configuration.Mapper;
import configuration.Query;
import exceptions.BindingException;

public class SqlSession {
	private class MapperHandler<T> implements InvocationHandler {
		Mapper instance;
		Class<T> type;
		Map<String, String> queryTypes;
		
		MapperHandler(Mapper instance, Class<T> type) {
			this.instance = instance;
			this.type = type;
			queryTypes = new HashMap<>();
			
			instance.queries.forEach((id, query) -> {
				queryTypes.put(id, query.queryType);
			});
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String mName = method.getName();
			Query query = instance.queries.get(mName);
			String sql = query.sql;
			
			switch (queryTypes.get(mName)) {
				case "select":
					Class returnType = Class.forName(query.resultType);
					return selectList(sql, returnType, args[0]);
				case "insert", "update", "delete":
					return executeUpdate(sql, args[0]);	
			}
			
			throw new BindingException("Illegal tag name");
		}
	}
	
	private static final String ARG_PAT = "#\\{[\\w]\\w*}";
	private static final String PRMTV_SEQ = "#{value}";
	
	private Connection con;
	private Map<String, Mapper> mappers;
	private Pattern paramPat;
	
	SqlSession(Connection con, Map<String, Mapper> mappers) {
		this.con = con;
		this.mappers = mappers;
		paramPat = Pattern.compile(ARG_PAT);
	}
	
	public <T> T selectOne(String sql, Class<T> c, Object params) throws Exception {
		List<T> resAL = selectList(sql, c, params);
		int size = resAL.size();
		
		if (size > 1)
			throw new SQLException("Too many results!");
		if (size == 0)
			return null;
		
		return resAL.get(0);
	}
	
	public <T> List<T> selectList(String sql, Class<T> c, Object params) throws Exception {
		T obj = null;
		ArrayList<T> resAL = new ArrayList<>();
		ResultSet rs;
		
		if (sql.contains(PRMTV_SEQ)) {
			sql = sql.replace(PRMTV_SEQ, "?");
			
			PreparedStatement ps = con.prepareStatement(sql);
			
			populatePreparedStatement(params, ps);
			
			rs = ps.executeQuery();	
		} else {
			sql = formatSql(sql, c, params);
			Statement query = con.createStatement();
			rs = query.executeQuery(sql);
		}
		
		Constructor<T> constructor = c.getDeclaredConstructor();
		obj = (T)constructor.newInstance();
		
		while (rs.next()) {
			obj = (T)constructor.newInstance();
			populateResult(c, rs, obj);
			resAL.add(obj);
		}
		
		return resAL;
	}
	
	private int executeUpdate(String sql, Object params) throws NoSuchFieldException, IllegalAccessException, SQLException {
		Statement query = null; 
		
		if (sql.contains(PRMTV_SEQ)) {
			if (!Number.class.isAssignableFrom(params.getClass()))
				sql = sql.replace(PRMTV_SEQ, String.format("\"%s\"", params.toString()));
			else
				sql = sql.replace(PRMTV_SEQ, params.toString());
		} else {
			sql = formatSql(sql, params.getClass(), params);
		}
		query = con.createStatement();
		
		return query.executeUpdate(sql);
	}
	
	private <T> String formatSql(String sql, Class<T> c, Object params) throws NoSuchFieldException, IllegalAccessException {
		Matcher paramMat = paramPat.matcher(sql);
		while (paramMat.find()) {
			String occ = paramMat.group();
			String fieldName = occ.substring(2, occ.length() - 1);
			
			Field f = c.getDeclaredField(fieldName);
			f.setAccessible(true);
			
			String val = f.get(params).toString();
			Class fcl = f.getType();
			
			if (!fcl.isPrimitive() && !Number.class.isAssignableFrom(fcl)) 
				val = String.format("\"%s\"", val);
			
			occ = occ.replaceAll("\\{", "\\\\{");
			sql = sql.replaceAll(occ, val);
		}
		
		return sql;
	}
	
	private void populatePreparedStatement(Object params, PreparedStatement ps) throws SQLException {
		Class fcl = params.getClass();
		if (fcl == Boolean.class)
			ps.setBoolean(1, (Boolean)params);
		else if (fcl == Byte.class)
			ps.setByte(1, (Byte)params);
		else if (fcl == Date.class)
			ps.setDate(1, (Date)params);
		else if (fcl == Double.class)
			ps.setDouble(1, (Double)params);
		else if (fcl == Float.class) 
			ps.setFloat(1, (Float)params);
		else if (fcl == Integer.class)
			ps.setInt(1, (Integer)params);
		else if (fcl == Long.class) 
			ps.setLong(1, (Long)params);
		else if (fcl == Short.class) 
			ps.setShort(1, (Short)params);
		else if (fcl == String.class) 
			ps.setString(1, (String)params);
		else if (fcl == Time.class)
			ps.setTime(1, (Time)params);
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
	
	public void close() throws SQLException {
		con.close();
		con = null;
		mappers = null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> cl) throws Exception {
		String clName = cl.getName();
		Mapper map = mappers.get(clName);
		
		if (map == null)
			return null;
		
		var handler = new MapperHandler<>(map, cl);
		T proxy = (T)Proxy.newProxyInstance(
			ClassLoader.getSystemClassLoader(),
			new Class[] {cl},
			handler
		);
		return proxy;
	}
}
