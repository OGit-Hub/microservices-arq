package com.library.exception;

public class BookIsNotRentedException extends RuntimeException{
	
	public BookIsNotRentedException(String errorMessage) {
        super(errorMessage);
    }

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
