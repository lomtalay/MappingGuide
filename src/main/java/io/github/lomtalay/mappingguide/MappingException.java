package io.github.lomtalay.mappingguide;

public class MappingException extends Exception {

	private static final long serialVersionUID = 1L;

	public MappingException(String msg) {
		super(msg);
	}
	
	public MappingException(String msg, Throwable t) {
		super(msg, t);
	}
}
