package com.dbsystel.tibco.plugin.cli;

public class DesignerCommandException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4503728756844291008L;

	/**
	 * 
	 * @param key
	 */
	public DesignerCommandException(String key) {
		super(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param cause
	 */
	public DesignerCommandException(String key, Throwable cause) {
		super(key, cause);
	}
	
	/**
	 * 
	 * @param cause
	 */
	public DesignerCommandException(Throwable cause) {
		super(cause);
	}

}
