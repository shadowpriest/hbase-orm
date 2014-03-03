package net.zhou.hbase;

import java.util.List;

import junit.framework.TestCase;
import net.zhou.dao.DaoException;

public class TestHbaseDao extends TestCase {

	public void testInsert() throws DaoException {
		char a = 0;
		char b='\u0000';
		assertEquals(b, a);
		Character c =a;
		final Person person = new Person();
		long key = 1234812785l;
		person.setId(key);
		person.setStringv("AAAAAAAAAAA");
		person.setBooleanv(true);
		person.setBytev((byte) 123);
		person.setShortv((short) 1234);
		person.setIntv(12345);
		person.setCharv('b');
		person.setLongv(123456);
		person.setFloatv(123456.7f);
		person.setDoublev(1234567.8d);
		
		final Person1 person1 = new Person1();
		person1.setId(key);
		person1.setStringv("AAAAAAAAAAA");
		person1.setBooleanv(true);
		person1.setBytev((byte) 123);
		person1.setShortv((short) 1234);
		person1.setIntv(12345);
		person1.setCharv('b');
		person1.setLongv(123456);
		person1.setFloatv(123456.7f);
		person1.setDoublev(1234567.8d);
//		person.setCreateAt(110);
		PersonDao dao = new PersonDao();
		Person1Dao dao1 = new Person1Dao();
		dao.put(person);
		dao1.put(person1);

		Person hperson = dao.get(key);
		assertEquals(person, hperson);
		
		Person1 hperson1 = dao1.get(key);
		assertEquals(person1, hperson1);

		List<Person> persons = dao.get(key, -1, Long.MAX_VALUE, 1000);
		System.out.println(persons);
		dao.delete(key);
		assertTrue(!dao.exist(key));
		dao.close();

		dao1.delete(key);
		assertTrue(!dao1.exist(key));
		dao1.close();
	}

}
