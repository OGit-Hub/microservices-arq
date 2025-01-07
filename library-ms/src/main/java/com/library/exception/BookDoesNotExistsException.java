package com.library.exception;

public class BookDoesNotExistsException extends RuntimeException {
	
	public BookDoesNotExistsException(String errorMessage) {
        super(errorMessage);
    }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
