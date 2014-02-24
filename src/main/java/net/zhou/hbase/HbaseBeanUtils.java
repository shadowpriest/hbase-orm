package net.zhou.hbase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import net.zhou.bean.BeanException;
import net.zhou.bean.BeanInfo;
import net.zhou.bean.BeanInfoFactory;
import net.zhou.bean.BytesAdapter;
import net.zhou.bean.FieldInfo;
import net.zhou.utils.InstanceResolver;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValue.SplitKeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * 
 * @author zhou
 * 
 */
public class HbaseBeanUtils {
	// private static Logger logger = Logger.getLogger(HbaseBeanUtils.class);
	private static final String ROWKEY = "rowkey";
	private static final String HAS_TIMESTAMP = "has_timestamp";
	private static final String TIMESTAMP = "timestamp";

	private static final String FQMAP = "FQMAP";
	private static final long DEFAULT_TIMESTAMP = HConstants.LATEST_TIMESTAMP;

	/**
	 * 将一个Bean对象转换成Put
	 * 
	 * @param object
	 * @return
	 */
	public static Put toPut(Object object) {
		Class<?> clazz = object.getClass();
		BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
		FieldInfo timestamp = getTimeStampField(beanInfo);
		if (timestamp == null) {
			return toPut(object, DEFAULT_TIMESTAMP);
		}
		long time = (Long) timestamp.getFieldValue(object);
		time = time <= 0 ? DEFAULT_TIMESTAMP : time;
		return toPut(object, time);
	}

	/**
	 * 将一个Bean对象转换成Put
	 * 
	 * @param object
	 * @return
	 */
	public static Put toPut(Object object, long timestamp) {
		Class<?> clazz = object.getClass();
		BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
		FieldInfo rowkey_fieldInfo = getRowKeyField(beanInfo);
		HRowField rowField = rowkey_fieldInfo.getAnnotation(HRowField.class);
		BytesAdapter rowKeyAdapter = InstanceResolver.getSingleton(rowField.adapter());
		byte[] rowKeys = rowKeyAdapter.toBytes(rowkey_fieldInfo.getFieldValue(object));
		Put put = new Put(rowKeys, timestamp);
		addFQ(put, beanInfo, object, rowkey_fieldInfo);
		return put;
	}

	/**
	 * 获取bean对象的rowkey转换定义
	 * 
	 * @param beanInfo
	 * @return
	 */
	public static BytesAdapter getRowKeyAdapter(BeanInfo beanInfo) {
		FieldInfo rowkey_fieldInfo = getRowKeyField(beanInfo);
		HRowField rowField = rowkey_fieldInfo.getAnnotation(HRowField.class);
		return InstanceResolver.getSingleton(rowField.adapter());
	}

	/**
	 * 获取一个bean的时间戳定义字段
	 * 
	 * @param beanInfo
	 * @return
	 */
	public static FieldInfo getTimeStampField(BeanInfo beanInfo) {
		Boolean has = (Boolean) beanInfo.getValue(HAS_TIMESTAMP);
		List<FieldInfo> fields = beanInfo.getFieldList();
		if (has == null) {
			int count = 0;
			FieldInfo timestamp = null;
			for (FieldInfo fieldInfo : fields) {
				if (fieldInfo.getAnnotation(HField.class) != null) {
					HField hfield = fieldInfo.getAnnotation(HField.class);
					if (hfield.timestamp()) {
						timestamp = fieldInfo;
						count++;
					}
				}
			}
			if (count > 1) {
				throw new BeanException("only a HField can be timestamp!");
			}
			has = count == 1;
			beanInfo.setValue(HAS_TIMESTAMP, has);
			if (has) {
				beanInfo.setValue(TIMESTAMP, timestamp);
				return timestamp;
			}
		}
		return has ? (FieldInfo) beanInfo.getValue(TIMESTAMP) : null;
	}

	/**
	 * 获取一个bean的rowkey字段
	 * 
	 * @param FieldInfo
	 * @return
	 */
	public static FieldInfo getRowKeyField(BeanInfo beanInfo) {
		FieldInfo rowkey_fieldInfo = (FieldInfo) beanInfo.getValue(ROWKEY);
		List<FieldInfo> fields = beanInfo.getFieldList();
		if (rowkey_fieldInfo == null) {
			for (FieldInfo fieldInfo : fields) {
				if (fieldInfo.getAnnotation(HRowField.class) != null) {
					rowkey_fieldInfo = fieldInfo;
					break;
				}
			}
			if (rowkey_fieldInfo == null) {
				throw new BeanException("please set a field as HRowField");
			}
			beanInfo.setValue(ROWKEY, rowkey_fieldInfo);
		}

		return rowkey_fieldInfo;
	}

	private static void addFQ(Put put, BeanInfo beanInfo, Object object, FieldInfo rowkey_fieldInfo) {
		List<FieldInfo> fields = beanInfo.getFieldList();
		for (FieldInfo fieldInfo : fields) {
			if (fieldInfo == rowkey_fieldInfo) {
				continue;

			}
			HField hfield = fieldInfo.getAnnotation(HField.class);
			if (hfield == null) {
				continue;
			}
			Object fieldValue = fieldInfo.getFieldValue(object);
			if (fieldValue == null || fieldInfo.defaultValue().equals(fieldValue)) {
				continue;
			}
			HFieldModel model = hfield.mode();
			byte[] value = null;
			if (model == HFieldModel.DEFAULT) {
				value = fieldInfo.dataType.toBytes(fieldValue);
			} else {
				Class<? extends BytesAdapter> adapterClazz = hfield.adapter();
				if (BytesAdapter.class != adapterClazz)
					value = InstanceResolver.getSingleton(adapterClazz).toBytes(fieldValue);
				else {
					throw new RuntimeException("please define adapter!!");
				}
			}
			if (ArrayUtils.isEmpty(value)) {
				continue;
			}
			put.add(Bytes.toBytes(hfield.f()), Bytes.toBytes(hfield.q()), value);
		}
	}

	/**
	 * 将一个result反序列化为一个对象
	 * 
	 * @param result
	 * @param clazz
	 * @return
	 */
	public static <T> T fromResult(Result result, Class<T> clazz) {
		if (result.isEmpty() || ArrayUtils.isEmpty(result.getRow())) {
			return null;
		}
		BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
		FieldInfo rowkey_fieldInfo = getRowKeyField(beanInfo);
		HRowField rowField = rowkey_fieldInfo.getAnnotation(HRowField.class);
		BytesAdapter rowKeyAdapter = InstanceResolver.getSingleton(rowField.adapter());
		T object = InstanceResolver.newInstance(clazz);
		rowkey_fieldInfo.setFieldValue(object, rowKeyAdapter.toObject(result.getRow()));
		readFieldFromResult(result, beanInfo, object);
		return object;
	}

	private static void readFieldFromResult(Result result, BeanInfo beanInfo, Object object) {
		NavigableMap<byte[], NavigableMap<byte[], byte[]>> res = result.getNoVersionMap();
		NavigableMap<byte[], NavigableMap<byte[], FieldInfo>> fqmap = getMap(beanInfo);
		for (Entry<byte[], NavigableMap<byte[], byte[]>> entry : res.entrySet()) {
			byte[] family = entry.getKey();
			NavigableMap<byte[], FieldInfo> map = fqmap.get(family);
			for (Entry<byte[], byte[]> b : entry.getValue().entrySet()) {
				FieldInfo fieldInfo = map.get(b.getKey());
				if (fieldInfo == null) {
					continue;
				}
				byte[] bytes = b.getValue();
				Object value = null;
				HField hfield = fieldInfo.getAnnotation(HField.class);
				HFieldModel model = hfield.mode();
				if (model == HFieldModel.DEFAULT) {
					value = fieldInfo.dataType.toObject(bytes);
				} else {
					Class<? extends BytesAdapter> adapterClazz = hfield.adapter();
					if (BytesAdapter.class != adapterClazz) {
						value = InstanceResolver.getSingleton(adapterClazz).toObject(bytes);
					} else {
						throw new RuntimeException("please define adapter!!");
					}
				}
				fieldInfo.setFieldValue(object, value);
			}
		}
	}

	private static NavigableMap<byte[], NavigableMap<byte[], FieldInfo>> getMap(BeanInfo beanInfo) {
		@SuppressWarnings("unchecked")
		NavigableMap<byte[], NavigableMap<byte[], FieldInfo>> res = (NavigableMap<byte[], NavigableMap<byte[], FieldInfo>>) beanInfo.getValue(FQMAP);
		if (res != null) {
			return res;
		}
		res = new TreeMap<byte[], NavigableMap<byte[], FieldInfo>>(Bytes.BYTES_COMPARATOR);
		List<FieldInfo> fields = beanInfo.getFieldList();
		for (FieldInfo fieldInfo : fields) {
			HField hfield = fieldInfo.getAnnotation(HField.class);
			if (hfield == null) {
				continue;
			}
			byte[] family = Bytes.toBytes(hfield.f());
			byte[] qualifier = Bytes.toBytes(hfield.q());
			if (res.get(family) == null) {
				res.put(family, new TreeMap<byte[], FieldInfo>(Bytes.BYTES_COMPARATOR));
			}
			res.get(family).put(qualifier, fieldInfo);
		}
		beanInfo.setValue(FQMAP, res);
		return res;
	}

	/**
	 * 根据版本，将一个Result反序列化为一群对象。 默认是降序。
	 * 
	 * @param result
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> fromVersionedResult(Result result, Class<T> clazz) {
		return fromVersionedResult(result, clazz, false);
	}

	/**
	 * 根据版本，将一个Result反序列化为一群对象。
	 * 
	 * @param result
	 * @param clazz
	 * @param asc
	 * @return
	 */
	public static <T> List<T> fromVersionedResult(Result result, Class<T> clazz, boolean asc) {
		if (result.isEmpty() || ArrayUtils.isEmpty(result.getRow())) {
			return new ArrayList<T>();
		}
		BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
		FieldInfo rowkey_fieldInfo = getRowKeyField(beanInfo);
		HRowField rowField = rowkey_fieldInfo.getAnnotation(HRowField.class);
		BytesAdapter rowKeyAdapter = InstanceResolver.getSingleton(rowField.adapter());
		Object rowValue = rowKeyAdapter.toObject(result.getRow());
		List<T> res = new ArrayList<T>();
		readFieldFromVersionedResult(result, beanInfo, res, asc);
		for (T t : res) {
			rowkey_fieldInfo.setFieldValue(t, rowValue);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private static <T> void readFieldFromVersionedResult(Result result, BeanInfo beanInfo, List<T> objects, boolean asc) {
		Class<T> clazz = (Class<T>) beanInfo.getClazz();
		KeyValue[] kvs = result.raw();
		NavigableMap<byte[], NavigableMap<byte[], FieldInfo>> fqmap = getMap(beanInfo);

		NavigableMap<Long, T> temp = asc ? new TreeMap<Long, T>() : new TreeMap<Long, T>(new Comparator<Long>() {

			@Override
			public int compare(Long o1, Long o2) {
				return o2.compareTo(o1);
			}
		});

		for (KeyValue kv : kvs) {
			SplitKeyValue splitKV = kv.split();
			byte[] family = splitKV.getFamily();
			NavigableMap<byte[], FieldInfo> map = fqmap.get(family);
			byte[] qualifier = splitKV.getQualifier();
			FieldInfo fieldInfo = map.get(qualifier);
			if (fieldInfo == null) {
				continue;
			}
			Long timestamp = Bytes.toLong(splitKV.getTimestamp());
			T object = temp.get(timestamp);
			if (object == null) {
				object = InstanceResolver.newInstance(clazz);
				temp.put(timestamp, object);
			}
			byte[] bytes = splitKV.getValue();
			Object value = null;
			HField hfield = fieldInfo.getAnnotation(HField.class);
			HFieldModel model = hfield.mode();
			if (model == HFieldModel.DEFAULT) {
				value = fieldInfo.dataType.toObject(bytes);
			} else {
				Class<? extends BytesAdapter> adapterClazz = hfield.adapter();
				if (BytesAdapter.class != adapterClazz) {
					value = InstanceResolver.getSingleton(adapterClazz).toObject(bytes);
				} else {
					throw new RuntimeException("please define adapter!!");
				}
			}
			fieldInfo.setFieldValue(object, value);
		}
		FieldInfo tsField = getTimeStampField(beanInfo);
		if (tsField == null) {
			for (Entry<Long, T> entry : temp.entrySet()) {
				objects.add(entry.getValue());
			}
		} else {
			for (Entry<Long, T> entry : temp.entrySet()) {
				tsField.setFieldValue(entry.getValue(), entry.getKey());
				objects.add(entry.getValue());
			}
		}
	}

}
