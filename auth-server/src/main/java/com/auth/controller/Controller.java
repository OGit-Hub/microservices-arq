package com.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.TokenDto;
import com.auth.dto.UserDto;
import com.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
public class Controller {
	
	@Autowired
	AuthService service;
	
	@PostMapping("/create")
	public ResponseEntity<UserDto> create(@RequestBody UserDto dto){
		UserDto result = service.saveUser(dto);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@RequestBody UserDto dto){
		TokenDto result = service.login(dto);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/validate")
	public ResponseEntity<TokenDto> validate(@RequestParam String token){
		TokenDto result = service.validate(token);
		return ResponseEntity.ok(result);
	}

}
