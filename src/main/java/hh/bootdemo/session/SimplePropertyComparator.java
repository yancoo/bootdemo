/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.session;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.Sort.Direction;

import hh.bootdemo.exception.CommonExceptionType;
import hh.bootdemo.exception.ServiceException;

/**
 * simple property comparator
 *
 * @author yan
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SimplePropertyComparator<T> implements Comparator<T> {

	String propertyName;
	Direction direction;

	public static Map<Class, Map<String, Class>> typeMap = new HashMap<Class, Map<String, Class>>();

	Class type;

	public SimplePropertyComparator(String property, Direction direction) {
		this.propertyName = property;
		this.direction = direction;
	}

	public int compare(T o1, T o2) {
		try {

			if (null == typeMap.get(o1.getClass())) {
				Map typeMapTemp = getFiledsMap(o1);
				typeMap.put(o1.getClass(), typeMapTemp);
				type = (Class) typeMapTemp.get(propertyName);
			} else {
				type = (Class) typeMap.get(o1.getClass()).get(propertyName);
			}
			if (type == null) {
				throw new ServiceException(CommonExceptionType.internalError, "null type with " + propertyName);
			}

			String ex = BeanUtils.getProperty(o1, this.propertyName).toString();
			String v2 = BeanUtils.getProperty(o2, this.propertyName).toString();

			if (type.equals(String.class)) {
				int result = ex.compareTo(v2);
				return Direction.ASC.equals(this.direction) ? result : -result;
			} else if (type.equals(Integer.class) || type.equals(int.class)) {
				Integer exTmp = Integer.valueOf(ex);
				Integer v2Tmp = Integer.valueOf(v2);

				int result = exTmp < v2Tmp ? -1 : (exTmp == v2Tmp ? 0 : 1);
				return Direction.ASC.equals(this.direction) ? result : -result;
			} else if (type.equals(Long.class) || type.equals(long.class)) {
				Long exTmp = Long.valueOf(ex);
				Long v2Tmp = Long.valueOf(v2);

				int result = exTmp < v2Tmp ? -1 : (exTmp == v2Tmp ? 0 : 1);
				return Direction.ASC.equals(this.direction) ? result : -result;
			} else if (type.equals(Float.class) || type.equals(float.class)) {
				Float exTmp = Float.valueOf(ex);
				Float v2Tmp = Float.valueOf(v2);

				int result = exTmp < v2Tmp ? -1 : (exTmp == v2Tmp ? 0 : 1);
				return Direction.ASC.equals(this.direction) ? result : -result;
			} else if (type.equals(Double.class) || type.equals(double.class)) {
				Double exTmp = Double.valueOf(ex);
				Double v2Tmp = Double.valueOf(v2);

				int result = exTmp < v2Tmp ? -1 : (exTmp == v2Tmp ? 0 : 1);
				return Direction.ASC.equals(this.direction) ? result : -result;
			}

			return 0;
		} catch (ServiceException e) {
			throw e;
		} catch (Exception var6) {
			var6.printStackTrace();
			return 0;
		}
	}

	private Map getFiledsMap(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		Map map = new HashMap();
		for (int i = 0; i < fields.length; i++) {
			map.put(fields[i].getName(), fields[i].getType());
		}
		return map;
	}

}
