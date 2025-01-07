package com.library.exception;

public class BookAlreadyExistsException extends RuntimeException {
	
	public BookAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
