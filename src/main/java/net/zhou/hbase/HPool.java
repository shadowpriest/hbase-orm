package net.zhou.hbase;

import java.io.Closeable;
import java.io.IOException;

import net.zhou.bean.CloseUtils;
import net.zhou.dao.RuntimeDaoException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.apache.hadoop.hbase.client.HTablePool;

/**
 * 整个程序中，只使用一个HConnection实例
 * 
 * @author zhou
 * 
 */
@SuppressWarnings("deprecation")
public class HPool implements Closeable, HTableInterfaceFactory {
	private static HPool singleton = new HPool();

	public static HPool getInstance() {
		return singleton;
	}

	public static HTablePool createPool() {
		return new HTablePool(HBaseConfiguration.create(), Integer.MAX_VALUE, singleton);
	}

	/**
	 * clientpara是一个引用，如果其里面的参数改变，那么就有用了
	 * 
	 * @param clientpara
	 * @return
	 */
	public static HTablePool createPool(final ClientPara clientpara) {
		return new HTablePool(HBaseConfiguration.create(), Integer.MAX_VALUE, singleton) {

			@Override
			public HTableInterface getTable(String tableName) {
				HTableInterface table = super.getTable(tableName);
				if (!clientpara.autoflush) {
					table.setAutoFlush(clientpara.autoflush);
				}
				if (clientpara.writeBufferSize > 0) {
					try {
						table.setWriteBufferSize(clientpara.writeBufferSize);
					} catch (IOException e) {
						throw new RuntimeDaoException(e);
					}
				}
				return table;
			}

		};
	}

	private volatile HConnection connection;
	private volatile boolean closed = false;

	private HPool() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				close();
			}
		}));
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}
		CloseUtils.close(connection);
		connection = null;
		closed = true;
	}

	@Override
	public HTableInterface createHTableInterface(Configuration config, byte[] tableName) {
		if (connection == null) {
			synchronized (this) {
				if (connection == null) {
					try {
						connection = HConnectionManager.createConnection(config);
					} catch (Throwable e) {
						throw new RuntimeDaoException("init HConnection failed", e);
					}
				}
			}
		}
		try {
			return connection.getTable(tableName);
		} catch (Throwable e) {
			throw new RuntimeDaoException("get table failed", e);
		}
	}

	@Override
	public void releaseHTableInterface(HTableInterface table) throws IOException {
		table.close();
	}

}
