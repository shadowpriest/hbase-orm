package net.zhou.hadoop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SD is Serializer and Deserializer。 是字段的序列化反序列化标记
 * 
 * @author zhou
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HadoopField {
	/**
	 * 是否序列化
	 * @return
	 */
	boolean dps() default true;
}
