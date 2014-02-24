package net.zhou.bean;

import java.util.List;

import junit.framework.TestCase;
import net.zhou.dao.DaoException;
import net.zhou.hbase.HbaseDao;
import net.zhou.hbase.HbaseDaoImpl;

public class TestHbaseDao extends TestCase {

	public void testInsert() throws DaoException {
		final Person person = new Person();
		char a = 0;
		char b = '\u0000';
		assertEquals(b, a);
		Character c = a;
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
		PersonDao dao = new PersonDao();
		dao.put(person);

		Person hperson = dao.get(key);
		assertEquals(person, hperson);

		dao.delete(key);
		assertTrue(!dao.exist(key));

		dao.close();
	}

	public void testStaticCreateMethod() throws DaoException {
		final Person person = new Person();
		char a = 0;
		char b = '\u0000';
		assertEquals(b, a);
		Character c = a;
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
		HbaseDao<Person, Long> dao = HbaseDaoImpl.create("for_testcase_do_not_drop", Person.class, Long.class);
		dao.put(person);

		Person hperson = dao.get(key);
		assertEquals(person, hperson);

		dao.delete(key);
		//
		assertTrue(!dao.exist(key));

		dao.close();
	}

	public void testTimeStamp() throws DaoException {
		HbaseDao<Person, Long> dao = new PersonDao();
		final Person person1 = new Person();
		char a = 0;
		char b = '\u0000';
		assertEquals(b, a);
		long key = 1234812785l;
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
		person1.setCreateAt(110);

		dao.put(person1);
		System.out.println(dao.exist(key));
		
		Person hperson1 = dao.get(key);
		assertEquals(person1, hperson1);

		final Person person2 = new Person();
		person2.setId(key);
		person2.setStringv("AAAAAAAAAAA");
		person2.setBooleanv(true);
		person2.setBytev((byte) 123);
		person2.setShortv((short) 1234);
		person2.setIntv(12345);
		person2.setCharv('b');
		person2.setLongv(123456);
		person2.setFloatv(123456.7f);
		person2.setDoublev(1234567.8d);
		person2.setCreateAt(130);
		dao.put(person2);

		Person hperson = dao.get(key);
		List<Person> persons = dao.get(key, 0, Long.MAX_VALUE, 1000);
		System.out.println(persons);
		assertTrue(persons.size()==2);
		dao.delete(key);
		//
		assertTrue(!dao.exist(key));

		dao.close();
	}

}
