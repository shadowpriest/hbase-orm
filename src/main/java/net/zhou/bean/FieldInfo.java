package net.zhou.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 
 * @author zhou
 * 
 */
public class FieldInfo implements Comparable<FieldInfo> {

	private final String name;
	private final Class<?> clazz;
	private final Field field;
	private final Method setMethod;
	private final Method getMethod;
	public final DataType dataType;

	public FieldInfo(String name, Class<?> clazz, Field field, DataType dataType, Method setMethod, Method getMethod) {
		super();
		this.name = name;
		this.clazz = clazz;
		this.field = field;
		this.setMethod = setMethod;
		this.getMethod = getMethod;
		this.dataType = dataType;
	}

	public String toString() {
		return this.name + " " + dataType;
	}

	public String getName() {
		return name;
	}

	public Field getField() {
		return field;
	}

	public int compareTo(FieldInfo o) {
		return this.name.compareTo(o.name);
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return field.getAnnotation(annotationClass);
	}

	private static final Object[] EMPYT = new Object[0];

	public Object getFieldValue(Object javaObject) {
		try {
			if (getMethod != null) {
				Object value = getMethod.invoke(javaObject, EMPYT);
				return value;
			}
			return field.get(javaObject);
		} catch (Throwable e) {
			throw new BeanException(e);
		}
	}

	public void setFieldValue(Object javaObject, Object value) {
		try {
			if (setMethod != null) {
				setMethod.invoke(javaObject, new Object[] { value });
				return;
			}
			field.set(javaObject, value);
		} catch (Throwable e) {
			throw new BeanException(e);
		}
	}

	public Object defaultValue() {
		return dataType == null ? null : dataType.defaultValue();
	}

	public Class<?> getClazz() {
		return clazz;
	}

}
