package net.zhou.io;

import java.io.IOException;

import junit.framework.TestCase;
import net.zhou.hadoop.HadoopBeanUtis;
import net.zhou.protobuf.ProtobufBeanUtils;

public class TestSerializer extends TestCase {

	final Student student = new Student();

	@Override
	protected void setUp() throws Exception {
		student.setName("'sunwei");
		student.setAge(20);
		student.setHeight(120.12d);
		student.setId(123456979988l);
		student.setRank((short) 1234);
		student.setWeight(180f);
		// student.setTag("asdjkogkladjklgjklgadjklh");
		// student.setArrived(new AtomicBoolean(false));
		// student.setHaircount(new AtomicLong(123129874l));
		// student.setLikecount(new AtomicInteger(20));
		// System.out.println(DPS._JSON.toString(student));
	}

	public void testHadoop() throws IOException {
		byte[] jsonbytes = HadoopBeanUtis.toBytes(student);
		Student c = HadoopBeanUtis.fromBytes(jsonbytes, Student.class);
		assertEquals(student, c);
		System.out.println("hadoop length: " + jsonbytes.length);
	}

	public void testProtobuf() {
		byte[] jsonbytes = ProtobufBeanUtils.toBytes(student);
		Student c = ProtobufBeanUtils.fromBytes(jsonbytes, Student.class);
		System.out.println(c);
		assertEquals(student, c);

		System.out.println("protobuf length: " + jsonbytes.length);
	}

}
