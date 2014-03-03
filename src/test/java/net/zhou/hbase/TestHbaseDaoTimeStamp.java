package net.zhou.hbase;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import net.zhou.dao.DaoException;
import net.zhou.hbase.HbaseDaoImpl;

import org.apache.hadoop.hbase.util.Bytes;

public class TestHbaseDaoTimeStamp extends TestCase {

	public void testInsert() throws DaoException {
		char a = 0;
		char b = '\u0000';
		assertEquals(b, a);
		HbaseDaoImpl<Person2, Long> dao = new HbaseDaoImpl<Person2, Long>("for_testcase_do_not_drop2"){};
		long key = 1234812785l;
		byte[] keys = Bytes.toBytes(key);
		final Person2 first = new Person2();
		first.setId(key);
		first.setStringv("first");
		first.setCreateAt(1);

		final Person2 second = new Person2();
		second.setId(key);
		second.setStringv("second");
		second.setCreateAt(2);

		final Person2 third = new Person2();
		third.setId(key);
		third.setStringv("third");
		third.setCreateAt(3);

		dao.put(first);
		dao.put(second);
		dao.put(third);

		List<Person2> persons = dao.get(key, 1, 100, 1000);
		System.out.println(persons);
//		assertEquals(first, persons.get(2));
//		assertEquals(second, persons.get(1));
//		assertEquals(third, persons.get(0));

		dao.delele(Arrays.asList(keys), 1);
		
		persons = dao.get(key, 1, 4, 1000);
		System.out.println(persons);
		dao.close();
	}

}
