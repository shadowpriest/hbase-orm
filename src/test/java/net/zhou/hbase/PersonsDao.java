package net.zhou.hbase;

import net.zhou.hbase.HbaseDaoImpl;

public class PersonsDao extends HbaseDaoImpl<Person, Long> {

	public PersonsDao() {
		super("for_testcase_do_not_drop");
	}

}
