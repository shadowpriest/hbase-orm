package net.zhou.dao;

/**
 * 作为一个RuntimeException
 * @author zhou
 *
 */
public class RuntimeDaoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuntimeDaoException() {
		super();
	}

	public RuntimeDaoException(String msg) {
		super(msg);
	}

	public RuntimeDaoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public RuntimeDaoException(Throwable cause) {
		super(cause);
	}

}
