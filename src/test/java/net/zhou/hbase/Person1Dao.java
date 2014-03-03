package net.zhou.hbase;

import net.zhou.hbase.HbaseDaoImpl;

public class Person1Dao extends HbaseDaoImpl<Person1, Long> {

	public Person1Dao() {
		super("for_testcase_do_not_drop1");
	}


}
