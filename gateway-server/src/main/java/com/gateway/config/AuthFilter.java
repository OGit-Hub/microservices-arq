package com.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.dto.TokenDto;

import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GatewayFilter{
	
	public static final String AUTH_HEADER = "Authorization";
	
	@Autowired
	private WebClient.Builder webClient;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// TODO Auto-generated method stub
		if(!exchange.getRequest().getHeaders().containsKey(AUTH_HEADER)) {
			return onError(exchange, HttpStatus.BAD_REQUEST);
		}
		
		String tokenHeader = exchange.getRequest().getHeaders().get(AUTH_HEADER).get(0);
		String authChunks[] = tokenHeader.split(" ");
		
		if(authChunks.length != 2 || !authChunks[0].equals("Bearer")) {
			return onError(exchange, HttpStatus.BAD_REQUEST);
		}
		
		
		return webClient.build()
				.post()
				.uri("http://auth-server/auth/validate?token=" + authChunks[1])
				.retrieve()
				.bodyToMono(TokenDto.class)
				.map(m -> {
					return exchange;
				})
				.flatMap(chain::filter);
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status){
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		
		return response.setComplete();
	}
	
}
