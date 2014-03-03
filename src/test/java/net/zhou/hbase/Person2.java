package net.zhou.hbase;

import net.zhou.hbase.HField;
import net.zhou.hbase.HRowField;

public class Person2 {

	@HRowField(adapter = LongRowKeyAdapter.class)
	private long id;

	@HField(q = "stringv")
	private String stringv;

	
	@HField(q="create_at",timestamp=true)
	private long createAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStringv() {
		return stringv;
	}

	public void setStringv(String stringv) {
		this.stringv = stringv;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (createAt ^ (createAt >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((stringv == null) ? 0 : stringv.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person2 other = (Person2) obj;
		if (createAt != other.createAt)
			return false;
		if (id != other.id)
			return false;
		if (stringv == null) {
			if (other.stringv != null)
				return false;
		} else if (!stringv.equals(other.stringv))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Person2 [id=" + id + ", stringv=" + stringv + ", createAt=" + createAt + "]";
	}



	
	
	
}
