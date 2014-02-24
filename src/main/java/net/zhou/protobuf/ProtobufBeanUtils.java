package net.zhou.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.zhou.bean.BeanException;
import net.zhou.bean.BeanInfo;
import net.zhou.bean.BeanInfoFactory;
import net.zhou.bean.FieldInfo;
import net.zhou.utils.InstanceResolver;
import net.zhou.utils.UnSynByteArrayInputStream;
import net.zhou.utils.UnSynByteArrayOutputStream;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

/**
 * 
 * @author zhou
 * 
 */
public class ProtobufBeanUtils {
	public static void toStream(OutputStream outputStrean, Object object) throws IOException {
		try {
			Class<?> clazz = object.getClass();
			BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
			CodedOutputStream out = CodedOutputStream.newInstance(outputStrean);
			List<FieldInfo> fields = beanInfo.getFieldList();
			for (int i = 0, size = fields.size(); i < size; i++) {
				FieldInfo fieldInfo = fields.get(i);
				ProtobufField protobufField = fieldInfo.getAnnotation(ProtobufField.class);
				if (protobufField != null && protobufField.index() > 0) {
					Object fieldValue = fieldInfo.getFieldValue(object);
					if (fieldValue != null && !fieldValue.equals(fieldInfo.defaultValue())) {
						fieldInfo.dataType.writePB(fieldValue, protobufField.index(), out);
					}
				}
			}
			out.flush();
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public static <T> T fromStream(InputStream inputStream, Class<T> clazz) throws IOException {
		try {
			CodedInputStream input = CodedInputStream.newInstance(inputStream);
			BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
			T targetObject = InstanceResolver.newInstance(clazz);
			while (true) {
				final int tag = input.readTag();
				if (tag == 0) {
					break;
				}
				int fieldIndex = WireFormat.getTagFieldNumber(tag);
				FieldInfo fieldInfo = beanInfo.getByPBIndex(fieldIndex);
				Object fieldValue = fieldInfo.dataType.readPB(input);
				fieldInfo.setFieldValue(targetObject, fieldValue);
			}
			return targetObject;
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public static byte[] toBytes(Object object) {
		try {
			UnSynByteArrayOutputStream outputStrean = new UnSynByteArrayOutputStream();
			toStream(outputStrean, object);
			return outputStrean.toByteArray();
		} catch (IOException e) {
			throw new BeanException(e);
		}
	}

	public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
		try {
			return fromStream(new UnSynByteArrayInputStream(bytes), clazz);
		} catch (Throwable e) {
			throw new BeanException(e);
		}
	}
}
