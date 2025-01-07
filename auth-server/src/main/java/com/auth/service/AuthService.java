package com.auth.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.auth.config.JwtProvider;
import com.auth.dto.TokenDto;
import com.auth.dto.UserDto;
import com.auth.entity.UserEntity;
import com.auth.repository.UserRepository;

@Service
public class AuthService {
	
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private JwtProvider provider;
	
	public UserDto saveUser(UserDto dto) {
		Optional<UserEntity> result = repository.findByUsername(dto.getUsername());
		
		if(result.isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("User %s already exists", dto.getUsername()));
		}
		
		UserEntity entity = repository.save(new UserEntity(dto.getUsername(), encoder.encode(dto.getPassword())));
		
		return mapper.map(entity, UserDto.class);
		
	}
	
	public TokenDto login(UserDto dto) {
		
		Optional<UserEntity> result = repository.findByUsername(dto.getUsername());
		
		if(!result.isPresent()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("User %s not exists", dto.getUsername()));
		}
		
		if(encoder.matches(dto.getPassword(), result.get().getPassword())) {
			return new TokenDto(provider.create(result.get()));
		}
		
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		
	}
	
	public TokenDto validate(String token) {
		if(!provider.validate(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		
		String username = provider.getUsernameFromToken(token);
		Optional<UserEntity> result = repository.findByUsername(username);
		
		if(!result.isPresent()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		
		return new TokenDto(token);
		
		
	}
	
	

}
