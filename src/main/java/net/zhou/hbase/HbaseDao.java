package net.zhou.hbase;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;

import net.zhou.dao.DaoException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;

/**
 * T是数据库序列化、反序列化对象。K是hbase的rowkey实际对应的对象。
 * @author zhou
 * 
 */
public interface HbaseDao<T, K> extends Closeable {

	public String getTableName();
	

	/**
	 * 获取rowkey
	 * @param key
	 * @return
	 */
	public byte[] rowKey(K key);

	boolean exist(K key) throws DaoException;
	
	boolean exist(byte[] key) throws DaoException;

	T get(K key) throws DaoException;

	T get(byte[] key) throws DaoException;
	
	/**
	 * 获取不同版本的所有对象
	 * @param key
	 * @param minStamp >0
	 * @param maxStamp <Long.MAX_VALUE
	 * @param maxVersions
	 * @return
	 * @throws DaoException
	 */
	List<T> get(K key, long minStamp, long maxStamp, int maxVersions)
			throws DaoException;
	
	/**
	 * 获取不同版本的所有对象
	 * @param key
	 * @param minStamp >0
	 * @param maxStamp <Long.MAX_VALUE
	 * @param maxVersions
	 * @return
	 * @throws DaoException
	 */
	List<T> get(byte[] key, long minStamp, long maxStamp, int maxVersions)
			throws DaoException;

	Result getResult(byte[] key) throws DaoException;

	/**
	 * 返回值和传参是一一对应的,即使某些key不存在，也会塞一个空的对象进去
	 * 
	 * @param keys
	 * @return
	 * @throws DaoException
	 */
	List<T> getList(Collection<K> keys) throws DaoException;

	List<T> getList(Scan scan) throws DaoException;

	void put(T obj) throws DaoException;

	void put(List<T> objs) throws DaoException;

	void putMeta(List<Put> puts) throws DaoException;


	void delete(K key) throws DaoException;

	void delete(byte[] key) throws DaoException;

	void delele(List<K> keys) throws DaoException;

	/**
	 * without excetion
	 */
	public void close();
	
	
	public boolean isAutoflush();

	public void setAutoflush(boolean autoflush);

	public long getWriteBufferSize();

	public void setWriteBufferSize(long writeBufferSize);
}
