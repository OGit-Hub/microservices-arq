package com.client.domain;

public class BookDto {
	
	private int id;
	private String title;
	private String author;
	private boolean isRented;
	private int clientId;
	
	/**
	 * 
	 */
	public BookDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param author
	 */
	public BookDto(String title, String author) {
		super();
		this.title = title;
		this.author = author;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	/**
	 * @return the isReented
	 */
	public boolean isRented() {
		return isRented;
	}
	/**
	 * @param isReented the isReented to set
	 */
	public void setRented(boolean isRented) {
		this.isRented = isRented;
	}
	/**
	 * @return the clientId
	 */
	public int getClientId() {
		return clientId;
	}
	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	

}
