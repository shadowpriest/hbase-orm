package net.zhou.hbase;

import net.zhou.hbase.HField;
import net.zhou.hbase.HRowField;

public class Person1 {

	@HRowField(adapter = LongRowKeyAdapter.class)
	private long id;

	@HField(q = "stringv",f="b")
	private String stringv;

	@HField(q = "booleanv",f="b")
	private boolean booleanv;

	@HField(q = "bytev",f="b")
	private byte bytev;
	
	@HField(q = "charv",f="b")
	private char charv;
	
	@HField(q = "shortv",f="b")
	private short shortv;
	
	@HField(q = "intv",f="b")
	private int intv;

	@HField(q = "longv",f="b")
	private long longv;

	@HField(q = "floatv",f="b")
	private float floatv;

	@HField(q = "doublev",f="b")
	private double doublev;
	
	@HField(q="create_at",timestamp=true,f="b")
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

	public boolean isBooleanv() {
		return booleanv;
	}

	public void setBooleanv(boolean booleanv) {
		this.booleanv = booleanv;
	}

	public byte getBytev() {
		return bytev;
	}

	public void setBytev(byte bytev) {
		this.bytev = bytev;
	}

	public short getShortv() {
		return shortv;
	}

	public void setShortv(short shortv) {
		this.shortv = shortv;
	}

	public int getIntv() {
		return intv;
	}

	public void setIntv(int intv) {
		this.intv = intv;
	}

	public long getLongv() {
		return longv;
	}

	public void setLongv(long longv) {
		this.longv = longv;
	}

	public float getFloatv() {
		return floatv;
	}

	public void setFloatv(float floatv) {
		this.floatv = floatv;
	}

	public double getDoublev() {
		return doublev;
	}

	public void setDoublev(double doublev) {
		this.doublev = doublev;
	}


	public char getCharv() {
		return charv;
	}

	public void setCharv(char charv) {
		this.charv = charv;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (booleanv ? 1231 : 1237);
		result = prime * result + bytev;
		result = prime * result + charv;
		long temp;
		temp = Double.doubleToLongBits(doublev);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Float.floatToIntBits(floatv);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + intv;
		result = prime * result + (int) (longv ^ (longv >>> 32));
		result = prime * result + shortv;
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
		Person1 other = (Person1) obj;
		if (booleanv != other.booleanv)
			return false;
		if (bytev != other.bytev)
			return false;
		if (charv != other.charv)
			return false;
		if (Double.doubleToLongBits(doublev) != Double.doubleToLongBits(other.doublev))
			return false;
		if (Float.floatToIntBits(floatv) != Float.floatToIntBits(other.floatv))
			return false;
		if (id != other.id)
			return false;
		if (intv != other.intv)
			return false;
		if (longv != other.longv)
			return false;
		if (shortv != other.shortv)
			return false;
		if (stringv == null) {
			if (other.stringv != null)
				return false;
		} else if (!stringv.equals(other.stringv))
			return false;
		return true;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", stringv=" + stringv + ", createAt=" + createAt + "]";
	}


	
	
	
}
