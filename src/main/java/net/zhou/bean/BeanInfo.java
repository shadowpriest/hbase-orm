package net.zhou.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.zhou.protobuf.ProtobufField;

public class BeanInfo {

	public final Class<?> clazz;

	private final List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
	private final List<FieldInfo> indexedFieldList = new ArrayList<FieldInfo>();
	private final Map<String, Object> context = new HashMap<String, Object>(8);

	BeanInfo(Class<?> clazz) {
		super();
		this.clazz = clazz;
		for (int i = 0; i < 100; i++) {
			indexedFieldList.add(null);
		}
	}



	public Class<?> getClazz() {
		return clazz;
	}

	public List<FieldInfo> getFieldList() {
		return fieldList;
	}

	public List<FieldInfo> getSortedFieldList() {
		return indexedFieldList;
	}

	public FieldInfo getByPBIndex(int index) {
		return indexedFieldList.get(index);
	}

	public FieldInfo getField(String propertyName) {
		for (FieldInfo item : this.fieldList) {
			if (item.getName().equals(propertyName)) {
				return item;
			}
		}
		return null;
	}

	public boolean add(FieldInfo field) {
		for (FieldInfo item : this.fieldList) {
			if (item.getName().equals(field.getName())) {
				return false;
			}
		}
		fieldList.add(field);
		ProtobufField protobufField = field.getAnnotation(ProtobufField.class);
		if (protobufField != null && protobufField.index() > 0) {
			indexedFieldList.set(protobufField.index(), field);
		}
		return true;
	}

	static BeanInfo compute(Class<?> clazz) {
		BeanInfo beanInfo = new BeanInfo(clazz);

		for (Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			if (methodName.startsWith("set") && Character.isUpperCase(methodName.charAt(3))) {
				String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
				String lowPropertyName = propertyName.toLowerCase();
				Field field = getField(clazz, propertyName);
				if (field != null) {
					loop: for (Method method1 : clazz.getMethods()) {
						String methodName1 = method1.getName();
						if (methodName1.equals(methodName)) {
							continue;
						}
						String lowmethodName1 = methodName1.toLowerCase();
						if (Modifier.isStatic(method1.getModifiers())) {
							continue;
						}

						if (lowmethodName1.contains(lowPropertyName)) {
							if (methodName1.startsWith("get") && Character.isUpperCase(methodName1.charAt(3))) {
								String propertyName1 = Character.toLowerCase(methodName1.charAt(3)) + methodName1.substring(4);
								Field field1 = getField(clazz, propertyName1);
								if (field.equals(field1)) {
									DataType dataType = DataType.getDataType(field.getType());
									beanInfo.add(new FieldInfo(propertyName, clazz, field, dataType, method, method1));
									break loop;
								}
							} else if (methodName1.startsWith("is") && Character.isUpperCase(methodName1.charAt(2))) {
								String propertyName1 = Character.toLowerCase(methodName1.charAt(2)) + methodName1.substring(3);
								Field field1 = getField(clazz, propertyName1);
								if (field.equals(field1)) {
									DataType dataType = DataType.getDataType(field.getType());
									beanInfo.add(new FieldInfo(propertyName, clazz, field, dataType, method, method1));
									break loop;
								}
							}
						}
					}
				}
			}
		}

		return beanInfo;
	}


	public static Field getField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			return null;
		}
	}


	public Object getValue(String key) {
		return context.get(key);
	}

	public void setValue(String key, Object obj) {
		context.put(key, obj);
	}

}
