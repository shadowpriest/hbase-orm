package net.zhou.hbase;

import net.zhou.hbase.HbaseDaoImpl;

public class PersonDao extends HbaseDaoImpl<Person, Long> {

	public PersonDao() {
		super("for_testcase_do_not_drop");
	}


}
