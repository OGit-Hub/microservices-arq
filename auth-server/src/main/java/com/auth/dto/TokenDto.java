package com.auth.dto;

public class TokenDto {
	
	private String token;
	
	public TokenDto() {
		
	}
	
	public TokenDto(String token) {
		this.token = token;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	

}
