package net.zhou.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.Format;

import net.zhou.utils.Constants;

import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 * 
 * @author zhou
 * 
 */
public enum DataType implements BytesAdapter {
	STRING("string", String.class) {

		@Override
		public Object defaultValue() {
			return "";
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((String) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toString(bytes);
		}

		@Override
		public String toString(Object object) {
			return (String) object;
		}

		@Override
		public Object toObject(String value) {
			return value;
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			String value = o == null ? "" : (String) o;
			Text.writeString(out, value);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return Text.readString(in);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			String value = o == null ? "" : (String) o;
			out.writeStringNoTag(value);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return in.readString();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			String value = o == null ? "" : (String) o;
			out.writeString(fieldNumber, value);
		}

	},

	BOOLEAN("boolean", Boolean.class) {

		@Override
		public Object defaultValue() {
			return Boolean.FALSE;
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((Boolean) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toBoolean(bytes);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Boolean.parseBoolean(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			boolean v = o == null ? false : ((Boolean) o).booleanValue();
			out.writeBoolean(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readBoolean();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			boolean v = o == null ? false : ((Boolean) o).booleanValue();
			out.writeBoolNoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return in.readBool();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			boolean v = o == null ? false : ((Boolean) o).booleanValue();
			out.writeBool(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	BYTE("byte", Byte.class) {

		@Override
		public Object defaultValue() {
			return Byte.valueOf((byte) 0);
		}

		@Override
		public byte[] toBytes(Object object) {
			return new byte[] { ((Byte) object).byteValue() };
		}

		@Override
		public Object toObject(byte[] bytes) {
			return ArrayUtils.isEmpty(bytes) ? 0 : bytes[0];
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Byte.parseByte(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			byte v = o == null ? 0 : ((Byte) o).byteValue();
			out.writeByte(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readByte();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			byte v = o == null ? 0 : ((Byte) o).byteValue();
			out.writeInt32NoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return (byte) in.readInt32();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			byte v = o == null ? 0 : ((Byte) o).byteValue();
			out.writeInt32(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	BYTES("bytearray", byte[].class) {

		@Override
		public Object defaultValue() {
			return ArrayUtils.EMPTY_BYTE_ARRAY;
		}

		@Override
		public byte[] toBytes(Object object) {
			return (byte[]) object;
		}

		@Override
		public Object toObject(byte[] bytes) {
			return bytes;
		}

		@Override
		public String toString(Object object) {
			return Bytes.toStringBinary((byte[]) object);
		}

		@Override
		public Object toObject(String value) {
			return Bytes.toBytesBinary(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			byte[] v = o == null ? ArrayUtils.EMPTY_BYTE_ARRAY : ((byte[]) o);
			Bytes.writeByteArray(out, v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return Bytes.readByteArray(in);
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			byte[] v = o == null ? ArrayUtils.EMPTY_BYTE_ARRAY : ((byte[]) o);
			out.writeBytesNoTag(ByteString.copyFrom(v));
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return in.readBytes().toByteArray();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			byte[] v = o == null ? Constants.EMPTY_BYTE_ARRAY : ((byte[]) o);
			out.writeBytes(fieldNumber, ByteString.copyFrom(v));
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	CHAR("char", Character.class) {

		@Override
		public Object defaultValue() {
			return Character.valueOf('\u0000');
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes(((Character) object).toString());
		}

		@Override
		public Object toObject(byte[] bytes) {
			String str = Bytes.toString(bytes);
			return str.charAt(0);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Character.valueOf(value.charAt(0));
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			char v = o == null ? 0 : ((Character) o).charValue();
			out.writeChar(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readChar();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			char v = o == null ? 0 : ((Character) o).charValue();
			out.writeInt32NoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return (char) in.readInt32();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			char v = o == null ? 0 : ((Character) o).charValue();
			out.writeInt32(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	SHORT("short", Short.class) {

		@Override
		public Object defaultValue() {
			return Short.valueOf((short) 0);
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((Short) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toShort(bytes);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Short.parseShort(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			short v = o == null ? 0 : ((Short) o).shortValue();
			out.writeShort(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readShort();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			short v = o == null ? 0 : ((Short) o).shortValue();
			out.writeInt32NoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return (short) in.readInt32();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			short v = o == null ? 0 : ((Short) o).shortValue();
			out.writeInt32(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	INT("int", Integer.class) {

		@Override
		public Object defaultValue() {
			return Integer.valueOf(0);
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((Integer) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toInt(bytes);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Integer.parseInt(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			int v = o == null ? 0 : ((Integer) o).intValue();
			out.writeInt(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readInt();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			int v = o == null ? 0 : ((Integer) o).intValue();
			out.writeInt32NoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return in.readInt32();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			int v = o == null ? 0 : ((Integer) o).intValue();
			out.writeInt32(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},
	LONG("long", Long.class) {

		@Override
		public Object defaultValue() {
			return Long.valueOf(0);
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((Long) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toLong(bytes);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Long.parseLong(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			long v = o == null ? 0 : ((Long) o).longValue();
			out.writeLong(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readLong();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			long v = o == null ? 0 : ((Long) o).longValue();
			out.writeInt64NoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return in.readInt64();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			long v = o == null ? 0 : ((Long) o).longValue();
			out.writeInt64(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	FLOAT("float", Float.class) {

		@Override
		public Object defaultValue() {
			return Float.valueOf(0f);
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((Float) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toFloat(bytes);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Float.parseFloat(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			float v = o == null ? 0 : ((Float) o).floatValue();
			out.writeFloat(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readFloat();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			float v = o == null ? 0 : ((Float) o).floatValue();
			out.writeFloatNoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return in.readFloat();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			float v = o == null ? 0 : ((Float) o).floatValue();
			out.writeFloat(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	DOUBLE("double", Double.class) {

		@Override
		public Object defaultValue() {
			return Double.valueOf(0d);
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((Double) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toDouble(bytes);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return Double.parseDouble(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			double v = o == null ? 0 : ((Double) o).doubleValue();
			out.writeDouble(v);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			return in.readDouble();
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			double v = o == null ? 0 : ((Double) o).doubleValue();
			out.writeDoubleNoTag(v);
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			return in.readDouble();
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			double v = o == null ? 0 : ((Double) o).doubleValue();
			out.writeDouble(fieldNumber, v);
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},

	BIGDECIMAL("BigDecimal", BigDecimal.class) {

		@Override
		public Object defaultValue() {
			return BigDecimal.valueOf(0);
		}

		@Override
		public byte[] toBytes(Object object) {
			return Bytes.toBytes((BigDecimal) object);
		}

		@Override
		public Object toObject(byte[] bytes) {
			return Bytes.toBigDecimal(bytes);
		}

		@Override
		public String toString(Object object) {
			return object.toString();
		}

		@Override
		public Object toObject(String value) {
			return new BigDecimal(value);
		}

		@Override
		public void write(Object o, DataOutput out) throws IOException {
			BigDecimal v = o == null ? BigDecimal.valueOf(0) : ((BigDecimal) o);
			byte[] bytes = Bytes.toBytes(v);
			Bytes.writeByteArray(out, bytes);
		}

		@Override
		public Object read(DataInput in) throws IOException {
			byte[] bytes = Bytes.readByteArray(in);
			return Bytes.toBigDecimal(bytes);
		}

		@Override
		public void writePB(Object o, CodedOutputStream out) throws IOException {
			BigDecimal v = o == null ? BigDecimal.valueOf(0) : ((BigDecimal) o);
			byte[] bytes = Bytes.toBytes(v);
			out.writeBytesNoTag(ByteString.copyFrom(bytes));
		}

		@Override
		public Object readPB(CodedInputStream in) throws IOException {
			byte[] bytes = in.readBytes().toByteArray();
			return Bytes.toBigDecimal(bytes);
		}

		@Override
		public void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException {
			BigDecimal v = o == null ? BigDecimal.valueOf(0) : ((BigDecimal) o);
			byte[] bytes = Bytes.toBytes(v);
			out.writeBytes(fieldNumber, ByteString.copyFrom(bytes));
		}

		@Override
		public String toStringLiteral(Format formatter) {
			return null;
		}

	},
	;

	private final String typeName;
	private final Class<?> clazz;

	private DataType(String typeName, Class<?> clazz) {
		this.typeName = typeName;
		this.clazz = clazz;
	}

	public abstract Object defaultValue();

	public abstract byte[] toBytes(Object object);

	public abstract Object toObject(byte[] bytes);

	public abstract String toString(Object object);

	public abstract Object toObject(String value);

	public abstract void write(Object o, DataOutput out) throws IOException;

	public abstract Object read(DataInput in) throws IOException;

	/*
	 * protobuf：没有tag
	 */
	public abstract void writePB(Object o, CodedOutputStream out) throws IOException;

	public abstract void writePB(Object o, int fieldNumber, CodedOutputStream out) throws IOException;

	public abstract Object readPB(CodedInputStream in) throws IOException;

	public Object toObject(Object object, DataType actualType) {
		if (this == actualType) {
			return object;
		}
		throw new IllegalArgumentException("Cannot convert from " + actualType + " to " + this);
	}

	public abstract String toStringLiteral(Format formatter);

	public final String getTypeName() {
		return typeName;
	}

	private final Class<?> getJavaClass() {
		return clazz;
	}

	public static DataType getDataType(Object value) {
		if (value == null) {
			return null;
		}
		for (DataType type : DataType.values()) {
			if (type.getJavaClass().isInstance(value)) {
				return type;
			}
		}
		return null;
	}

	public static DataType getDataType(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		if (clazz.isPrimitive()) {
			clazz = TypeUtils.getWapperClass(clazz);
		}
		for (DataType type : DataType.values()) {
			if (type.getJavaClass().equals(clazz)) {
				return type;
			}
		}
		return null;
	}

}
