package net.zhou.bean;

import net.zhou.hbase.HbaseDaoImpl;

/**
 * 
 * @author zhou
 * 
 */
public class PersonDao extends HbaseDaoImpl<Person, Long> {

	public PersonDao() {
		super("for_testcase_do_not_drop");
	}

}
