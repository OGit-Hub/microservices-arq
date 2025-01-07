package com.gateway.dto;

public class UserDto {
	
	private int id;
	private String username;
	private String password;
	
	/**
	 * @param id
	 * @param username
	 * @param password
	 */
	public UserDto(int id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * 
	 */
	public UserDto() {
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "UserDto [id=" + id + ", username=" + username + ", password=" + password + "]";
	}
	

}
