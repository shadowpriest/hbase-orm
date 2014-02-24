package net.zhou.hbase;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.zhou.bean.BytesAdapter;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HField {

	/**
	 * family
	 * 
	 * @return
	 */
	String f() default "a";

	/**
	 * qualifier
	 * 
	 * @return
	 */
	String q();
	
	boolean timestamp() default false;

	HFieldModel mode() default HFieldModel.DEFAULT;

	Class<? extends BytesAdapter> adapter() default BytesAdapter.class;
}
