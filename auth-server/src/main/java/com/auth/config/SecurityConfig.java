package com.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(crsf -> crsf.disable())
			.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
		http.headers(headers -> headers.frameOptions(option -> option.disable()));
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}
	
	
}
