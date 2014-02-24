package net.zhou.bean;

/**
 * 
 * @author zhou
 * 
 */
public interface BytesAdapter {

	public byte[] toBytes(Object value);

	public Object toObject(byte[] bytes);
}
