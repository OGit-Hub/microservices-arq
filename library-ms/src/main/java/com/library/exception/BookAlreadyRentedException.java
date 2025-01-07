package com.library.exception;

public class BookAlreadyRentedException extends RuntimeException{
	
	public BookAlreadyRentedException(String errorMessage) {
        super(errorMessage);
    }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
