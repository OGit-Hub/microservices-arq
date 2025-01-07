package com.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class RouteConfig {
	
	private static final Logger log = LoggerFactory.getLogger(RouteConfig.class);
	
	@Autowired
	private AuthFilter authFilter;
	
	@Bean
	@ConditionalOnProperty(prefix = "gateway", name = "route", havingValue = "static-routing")
	RouteLocator staticRouting(RouteLocatorBuilder builder) {
		log.info("Static routing configuration [UP]");
		
		return builder.routes()
			.route("library_route", r -> r.path("/library/**")
				.filters(f -> f.filter(authFilter))
				.uri("http://localhost:8080"))
			.route("client_route", r -> r.path("/client/**")
				.filters(f -> f.filter(authFilter))
				.uri("http://localhost:8081"))
			.build();
		
	}
	
	
	@Bean
	@ConditionalOnProperty(prefix = "gateway", name = "route", havingValue = "dynamic-routing")
	RouteLocator dynamicRouting(RouteLocatorBuilder builder) {
		log.info("Dynamic routing configuration [UP]");
		
		return builder.routes()
			.route("library_route", r -> r.path("/library/**")
				.filters(f -> f.filter(authFilter))
				.uri("lb://library-ms"))
			.route("client_route", r -> r.path("/client/**")
				.filters(f -> f.filter(authFilter))
				.uri("lb://client-ms"))
			.build();
	}
	
	
	@Bean
	@ConditionalOnProperty(prefix = "gateway", name = "route", havingValue = "circuit-routing")
	RouteLocator dynamicRoutingCB(RouteLocatorBuilder builder) {
		log.info("Dynamic routing (Circuit Breaking) configuration [UP]");
		
		return builder.routes()
			.route("library_route", r -> r.path("/library/**")
				.filters(f -> f.circuitBreaker(c -> c.setName("failoverCB")
						.setFallbackUri(UriComponentsBuilder.fromUriString("forward:/library-failover").build().toUri())
						.setRouteId("libraryFailover"))
					.filter(authFilter))
				.uri("lb://library-ms"))
			.route("library_failover_route", r -> r.path("/library-failover/**")
				.filters(f -> f.filter(authFilter))
				.uri("lb://library-failover-ms"))
			.route("client_route", r -> r.path("/client/**")
				.filters(f -> f.filter(authFilter))
				.uri("lb://client-ms"))
			.build();
	}

}
