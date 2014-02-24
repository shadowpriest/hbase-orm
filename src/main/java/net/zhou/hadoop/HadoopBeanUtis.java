package net.zhou.hadoop;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.zhou.bean.BeanInfo;
import net.zhou.bean.BeanInfoFactory;
import net.zhou.bean.FieldInfo;
import net.zhou.utils.InstanceResolver;
import net.zhou.utils.UnSynByteArrayInputStream;
import net.zhou.utils.UnSynByteArrayOutputStream;

/**
 * 
 * @author zhou
 * 
 */
public class HadoopBeanUtis {
	public static void write(Object object, DataOutput output) throws IOException {
		try {
			Class<?> clazz = object.getClass();
			BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
			List<FieldInfo> fields = beanInfo.getFieldList();
			int size = fields.size();
			for (int i = 0; i < size; i++) {
				FieldInfo fieldInfo = fields.get(i);
				if (fieldInfo.dataType == null) {
					continue;
				}
				HadoopField hadoopField = fieldInfo.getAnnotation(HadoopField.class);
				if (hadoopField == null || hadoopField.dps()) {
					Object fieldValue = fieldInfo.getFieldValue(object);
					fieldInfo.dataType.write(fieldValue, output);
				}
			}
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public static void readFields(Object object, DataInput input) throws IOException {
		try {
			BeanInfo beanInfo = BeanInfoFactory.compute(object.getClass());
			List<FieldInfo> fields = beanInfo.getFieldList();
			int size = fields.size();
			for (int i = 0; i < size; i++) {
				FieldInfo fieldInfo = fields.get(i);
				if (fieldInfo.dataType == null) {
					continue;
				}
				HadoopField hadoopField = fieldInfo.getAnnotation(HadoopField.class);
				if (hadoopField == null || hadoopField.dps()) {
					Object fieldValue = fieldInfo.dataType.read(input);
					fieldInfo.setFieldValue(object, fieldValue);
				}
			}
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public static byte[] toBytes(Object object) throws IOException {
		UnSynByteArrayOutputStream outputStrean = new UnSynByteArrayOutputStream();
		DataOutput output = new DataOutputStream(outputStrean);
		write(object, output);
		return outputStrean.toByteArray();
	}

	public static <T> T fromBytes(byte[] bytes, Class<T> clazz) throws IOException {
		DataInput input = new DataInputStream(new UnSynByteArrayInputStream(bytes));
		T object = InstanceResolver.newInstance(clazz);
		readFields(object, input);
		return object;
	}

}
