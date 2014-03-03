package net.zhou.hbase;

public class ClientPara {
	public boolean autoflush = true;
	public long writeBufferSize = -1;
	
	

	public ClientPara() {
		super();
	}

	public ClientPara(boolean autoflush, long writeBufferSize) {
		super();
		this.autoflush = autoflush;
		this.writeBufferSize = writeBufferSize;
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

}
