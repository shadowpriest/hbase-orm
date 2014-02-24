package net.zhou.bean;

public class LongRowKeyAdapter implements BytesAdapter {

	@Override
	public byte[] toBytes(Object value) {
		return DataType.LONG.toBytes(value);
	}

	@Override
	public Object toObject(byte[] bytes) {
		return DataType.LONG.toObject(bytes);
	}

}
