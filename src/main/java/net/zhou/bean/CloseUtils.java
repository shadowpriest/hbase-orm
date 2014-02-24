package net.zhou.bean;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author zhou
 * 
 */
public abstract class CloseUtils {

	public static void close(Closeable close) {
		if (close != null) {
			try {
				close.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(Connection close) {
		if (close != null) {
			try {
				close.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(ResultSet close) {
		if (close != null) {
			try {
				close.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void close(Statement close) {
		if (close != null) {
			try {
				close.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}
