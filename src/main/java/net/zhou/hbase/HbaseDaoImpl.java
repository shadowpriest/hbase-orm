package net.zhou.hbase;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.zhou.bean.BeanInfo;
import net.zhou.bean.BeanInfoFactory;
import net.zhou.bean.BytesAdapter;
import net.zhou.bean.CloseUtils;
import net.zhou.bean.TypeUtils;
import net.zhou.dao.DaoException;
import net.zhou.dao.RuntimeDaoException;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.http.annotation.ThreadSafe;

/**
 * 
 * @author zhou
 * 
 * @param <T>  T是数据库序列化、反序列化对象。
 * @param <K>  K是hbase的rowkey实际对应的对象。
 */
@SuppressWarnings({ "unchecked", "deprecation" })
@ThreadSafe
public class HbaseDaoImpl<T, K> implements HbaseDao<T, K> {
	private final String tableName;
	protected Class<T> clazz;
	private boolean autoflush = true;
	private long writeBufferSize = -1;
	protected HTablePool tablePool;
	private BytesAdapter rowKeyAdapter;

	public HbaseDaoImpl(String tableName) {
		this(tableName, null, null);
	}

	private HbaseDaoImpl(String tableName, Class<T> tclazz, Class<K> kclazz) {
		this.tableName = tableName;
		initClazz(tclazz, kclazz);
		tablePool = new HTablePool() {

			@Override
			public HTableInterface getTable(String tableName) {
				HTableInterface table = super.getTable(tableName);
				if (!autoflush) {
					table.setAutoFlush(autoflush);
				}
				if (writeBufferSize > 0) {
					try {
						table.setWriteBufferSize(writeBufferSize);
					} catch (IOException e) {
						throw new RuntimeDaoException(e);
					}
				}
				return table;
			}

		};
	}

	/**
	 * 根据tablename和类型直接生成一个dao
	 * 
	 * @param tablename
	 * @param tclazz T是数据库序列化、反序列化对象。
	 * @return
	 */
	public static <T> HbaseDao<T, Object> create(final String tablename, Class<T> kclazz) {
		return create(tablename, kclazz, Object.class);
	}

	/**
	 * 
	 * @param tablename
	 * @param tclazz 
	 * @param kclass
	 * @return
	 */
	public static <T, K> HbaseDao<T, K> create(final String tablename, Class<T> tclazz, Class<K> kclass) {
		return new HbaseDaoImpl<T, K>(tablename, tclazz, kclass);
	}

	private void initClazz(Class<T> tclazz, Class<K> kclazz) {
		if (tclazz != null) {
			clazz = tclazz;
		} else {
			Type mySuperClass = this.getClass().getGenericSuperclass();
			Type ttype = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
			clazz = (Class<T>) ttype;
		}
		BeanInfo beanInfo = BeanInfoFactory.compute(clazz);
		rowKeyAdapter = HbaseBeanUtils.getRowKeyAdapter(beanInfo);

		/*
		 * 这边检查HRowField定义的字段的类型和K是否一直，不一直就提醒用户修改成一致的！
		 */
		if (kclazz == null) {
			Type mySuperClass = this.getClass().getGenericSuperclass();
			Type ktype = ((ParameterizedType) mySuperClass).getActualTypeArguments()[1];
			kclazz = (Class<K>) ktype;
		}

		Class<?> rowkey_defined_kclass = HbaseBeanUtils.getRowKeyField(beanInfo).getField().getType();
		rowkey_defined_kclass = TypeUtils.getWapperClass(rowkey_defined_kclass);
		/*
		 * kclass.isAssignableFrom(rowkey_defined_kclass)的情况，
		 * 也就是kclass是rowkey_defined_kclass父类的情况， 会引起强制类型转换，可能会导致类型转换出错，应该抛出警报的
		 */
		if (rowkey_defined_kclass.isAssignableFrom(kclazz) || kclazz.isAssignableFrom(rowkey_defined_kclass)) {
			return;
		}
		throw new RuntimeDaoException("please sure HRowField's type equals to K");
	}

	@Override
	public boolean exist(K key) throws DaoException {
		return exist(rowKey(key));
	}

	@Override
	public boolean exist(byte[] key) throws DaoException {
		Get get = new Get(key);
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			return table.exists(get);
		} catch (Throwable e) {
			throw new DaoException("读取table_" + tableName + "出错。", e);
		} finally {
			CloseUtils.close(table);
		}
	}

	@Override
	public T get(K key) throws DaoException {
		return get(rowKey(key));
	}

	@Override
	public T get(byte[] key) throws DaoException {
		Get get = new Get(key);
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			Result result = table.get(get);
			return HbaseBeanUtils.fromResult(result, clazz);
		} catch (Throwable e) {
			throw new DaoException("读取table_" + tableName + "出错。", e);
		} finally {
			CloseUtils.close(table);
		}
	}

	@Override
	public List<T> get(K key, long minStamp, long maxStamp, int maxVersions) throws DaoException {
		return get(rowKey(key), minStamp, maxStamp, maxVersions);
	}

	@Override
	public List<T> get(byte[] key, long minStamp, long maxStamp, int maxVersions) throws DaoException {
		Get get = new Get(key);
		HTableInterface table = null;
		try {
			get.setTimeRange(minStamp, maxStamp).setMaxVersions(maxVersions);
			table = tablePool.getTable(tableName);
			Result result = table.get(get);
			return HbaseBeanUtils.fromVersionedResult(result, clazz);
		} catch (Throwable e) {
			throw new DaoException("读取table_" + tableName + "出错。", e);
		} finally {
			CloseUtils.close(table);
		}
	}

	@Override
	public Result getResult(byte[] key) throws DaoException {
		Get get = new Get(key);
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			return table.get(get);
		} catch (Throwable e) {
			throw new DaoException("读取table_" + tableName + "出错。", e);
		} finally {
			CloseUtils.close(table);
		}
	}

	@Override
	public List<T> getList(Collection<K> keys) throws DaoException {
		List<T> res = new ArrayList<T>();
		List<Get> gets = new ArrayList<Get>();
		for (K key : keys) {
			gets.add(new Get(rowKey(key)));
		}
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			Result[] results = table.get(gets);
			for (Result result : results) {
				res.add(HbaseBeanUtils.fromResult(result, clazz));
			}
		} catch (Throwable e) {
			throw new DaoException("读取出错。", e);
		} finally {
			CloseUtils.close(table);
		}
		return res;
	}

	@Override
	public List<T> getList(Scan scan) throws DaoException {
		List<T> res = new ArrayList<T>();
		ResultScanner rs = null;
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			rs = table.getScanner(scan);
			for (Result result : rs) {
				res.add(HbaseBeanUtils.fromResult(result, clazz));
			}
			return res;
		} catch (Throwable e) {
			throw new DaoException("读取出错。", e);
		} finally {
			CloseUtils.close(table);
			CloseUtils.close(rs);
		}
	}

	@Override
	public void put(T obj) throws DaoException {
		put(Arrays.asList(obj));
	}

	@Override
	public void put(List<T> objs) throws DaoException {
		List<Put> puts = new ArrayList<Put>();
		for (T t : objs) {
			puts.add(HbaseBeanUtils.toPut(t));
		}
		putMeta(puts);

	}

	@Override
	public void putMeta(List<Put> puts) throws DaoException {
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			table.put(puts);
		} catch (Throwable e) {
			throw new DaoException("写入出错。", e);
		} finally {
			CloseUtils.close(table);
		}
	}

	@Override
	public void delete(K key) throws DaoException {
		delele(Arrays.asList(key));
	}

	@Override
	public void delete(byte[] key) throws DaoException {
		Delete del = new Delete(key);
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			table.delete(del);
		} catch (Throwable e) {
			throw new DaoException("读取table_" + tableName + "出错。", e);
		} finally {
			CloseUtils.close(table);
		}

	}

	@Override
	public void delele(List<K> keys) throws DaoException {
		List<Delete> ds = new ArrayList<Delete>();
		for (K key : keys) {
			ds.add(new Delete(rowKey(key)));
		}
		HTableInterface table = null;
		try {
			table = tablePool.getTable(tableName);
			table.delete(ds);
		} catch (Throwable e) {
			throw new DaoException("删除出错", e);
		} finally {
			CloseUtils.close(table);
		}
	}

	@Override
	public void close() {
		CloseUtils.close(tablePool);
	}

	public boolean isAutoflush() {
		return autoflush;
	}

	public void setAutoflush(boolean autoflush) {
		this.autoflush = autoflush;
	}

	public long getWriteBufferSize() {
		return writeBufferSize;
	}

	public void setWriteBufferSize(long writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}

	@Override
	public byte[] rowKey(K key) {
		return rowKeyAdapter.toBytes(key);
	}

	/*
	 * 禁止Override
	 * 
	 * @see net.zhou.hbase.HbaseDao#getTableName()
	 */
	@Override
	public final String getTableName() {
		return tableName;
	}

	
	
	

}
