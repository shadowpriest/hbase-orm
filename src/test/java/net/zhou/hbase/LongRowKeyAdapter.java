package net.zhou.hbase;

import net.zhou.bean.BytesAdapter;

import org.apache.hadoop.hbase.util.Bytes;

public class LongRowKeyAdapter implements BytesAdapter {

	@Override
	public byte[] toBytes(Object value) {
		return Bytes.toBytes((Long) value);
	}

	@Override
	public Object toObject(byte[] bytes) {
		return Bytes.toLong(bytes);
	}

}
