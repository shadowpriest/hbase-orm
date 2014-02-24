package net.zhou.bean;

import java.util.IdentityHashMap;
import java.util.Map;


public class BeanInfoFactory {
	private static final Map<Class<?>, BeanInfo> beanInfos = new IdentityHashMap<Class<?>, BeanInfo>();

	public static BeanInfo compute(Class<?> clazz) {
		BeanInfo bi = beanInfos.get(clazz);
		if (bi != null) {
			return bi;
		}
		bi = BeanInfo.compute(clazz);
		beanInfos.put(clazz, bi);
		return bi;
	}
}
