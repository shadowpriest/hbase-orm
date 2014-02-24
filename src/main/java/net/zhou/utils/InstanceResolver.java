package net.zhou.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * 
 * 
 * {@link java.util.ServiceLoader}.
 * 
 * @author zhou
 * 
 */
@SuppressWarnings("unchecked")
public class InstanceResolver {
	private static Logger logger = Logger.getLogger(InstanceResolver.class);
	private static final ConcurrentMap<Class<?>, Object> RESOLVED_SINGLETONS = new ConcurrentIdentityHashMap<Class<?>, Object>();

	private InstanceResolver() {
	}

	public static <T> T getSingleton(Class<T> clazz, T defaultInstance) {
		Object obj = RESOLVED_SINGLETONS.get(clazz);
		if (obj != null) {
			return (T) obj;
		}
		if (defaultInstance != null && !clazz.isInstance(defaultInstance))
			throw new IllegalArgumentException("defaultInstance is not of type " + clazz.getName());
		final Object o = resolveSingleton(clazz, defaultInstance);
		obj = RESOLVED_SINGLETONS.putIfAbsent(clazz, o);
		if (obj == null) {
			obj = o;
		}
		return (T) obj;
	}

	public static <T> T getSingleton(Class<T> clazz) {
		Object obj = RESOLVED_SINGLETONS.get(clazz);
		if (obj != null) {
			return (T) obj;
		}
		final Object o = resolveSingleton(clazz);
		obj = RESOLVED_SINGLETONS.putIfAbsent(clazz, o);
		if (obj == null) {
			obj = o;
		}
		return (T) obj;
	}

	private synchronized static <T> T resolveSingleton(Class<T> clazz, T defaultInstance) {
		ServiceLoader<T> loader = ServiceLoader.load(clazz);
		for (T singleton : loader) {
			return singleton;
		}
		return defaultInstance;
	}

	private synchronized static <T> T resolveSingleton(Class<T> clazz) {
		ServiceLoader<T> loader = ServiceLoader.load(clazz);
		for (T singleton : loader) {
			return singleton;
		}
		return newInstance(clazz);
	}
	
	public static Object newInstance(String classname) {
		try {
			return newInstance(Class.forName(classname));
		} catch (ClassNotFoundException e) {
			logger.warn(e);
		}
		return null;
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			logger.warn(e);
		} catch (IllegalAccessException e) {
			Constructor<T> constructor = getDefaultConstructor(clazz);
			try {
				return constructor.newInstance(ArrayUtils.EMPTY_OBJECT_ARRAY);
			} catch (Exception e1) {
				logger.warn(e);
			}
		}
		return null;
	}

	private static ConcurrentMap<Class<?>, Constructor<?>> cache = new ConcurrentIdentityHashMap<Class<?>, Constructor<?>>();

	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
		Constructor<?> res = cache.get(clazz);
		if (res != null) {
			return (Constructor<T>) res;
		}
		if (Modifier.isAbstract(clazz.getModifiers())) {
			return null;
		}

		Constructor<?> defaultConstructor = null;
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (constructor.getParameterTypes().length == 0) {
				defaultConstructor = constructor;
				break;
			}
		}

		if (defaultConstructor == null) {
			if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
				for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
					if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].equals(clazz.getDeclaringClass())) {
						defaultConstructor = constructor;
						break;
					}
				}
			}
		}
		defaultConstructor.setAccessible(true);
		Constructor<?> obj = cache.putIfAbsent(clazz, defaultConstructor);
		if (obj != null) {
			defaultConstructor = obj;
		}
		return (Constructor<T>) defaultConstructor;
	}
}
