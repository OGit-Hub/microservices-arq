# Spring Cloud Gateway

Spring Cloud Gateway aims to provide a simple, yet effective way to route to APIs and provide cross cutting concerns to them such as: security, monitoring/metrics, and resiliency.

### Spring Cloud Gateway Routes

In this part, we will see a few types of filters that we can make in HTTP requests to the Gateway server both directly and using Eureka service discovery.

Anyway, we will need to create a configuration class.
```
@Configuration
public class RouteConfig {
```

**Static routing**

In this case, we will route direclty to the webservices site, wihtout using eureka server
```
@Bean
@ConditionalOnProperty(prefix = "gateway", name = "route", havingValue = "static-routing")
RouteLocator staticRouting(RouteLocatorBuilder builder) {
	log.info("Static routing configuration [UP]");
	
	return builder.routes()
		.route("library_route", r -> r.path("/library/**")
			.uri("http://localhost:8080"))
		.build();
	
}
```

**Dynamic routing**

In this case, we will route to the webservices site using Eureka Discovery Client as `lb://`
```
@Bean
@ConditionalOnProperty(prefix = "gateway", name = "route", havingValue = "dynamic-routing")
RouteLocator dynamicRouting(RouteLocatorBuilder builder) {
	log.info("Dynamic routing configuration [UP]");
	
	return builder.routes()
		.route("library_route", r -> r.path("/library/**")
			.uri("lb://library"))
		.build();
}
```

**Circuit Breaking routing**

As in the previous case, we will use a dynamic routing using Eureka Discovery Client but in this case we will configure a fallback uri which will take place in case the first is not available
```
@Bean
@ConditionalOnProperty(prefix = "gateway", name = "route", havingValue = "circuit-routing")
RouteLocator dynamicRoutingCB(RouteLocatorBuilder builder) {
	log.info("Dynamic routing (Circuit Breaking) configuration [UP]");
	
	return builder.routes()
		.route("library_app_name", r -> r.path("/library/**")
			.filters(f -> f.circuitBreaker(c -> c.setName("failoverCB")
				.setFallbackUri("forward:/library-failover/")
				.setRouteId("libraryFailover")))
			.uri("lb://library"))
		.route("library_failover_route", r -> r.path("/library-failover/**").uri("lb://library-failover"))
		.build();
}
```

### Gateway filter - Authentication
To be able to force authentication in requests made to our gateway server, we will be aplying a filter (like the ones we see before in routing to set a fallback service) to everyone of them. First, we need to configure that filter using reactive programming with Spring Webflux.

```
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
```
In a @Component class we create a class that implements **GatewayFilter** and override the metod **filter**. What we are doing is checking if request headers has 'Auhtorization' header with the format 'Beared {token}'. 
In case header format is valid, we will use Auth Server to validate that token.

After that, we will aply that filter to the routing method we were using.

```
@Bean
@ConditionalOnProperty(prefix = "gateway", name = "route", havingValue = "circuit-routing")
RouteLocator dynamicRoutingCB(RouteLocatorBuilder builder) {
	log.info("Dynamic routing (Circuit Breaking) configuration [UP]");
	
	return builder.routes()
		.route("library_app_name", r -> r.path("/library/**")
			.filters(f -> f.circuitBreaker(c -> c.setName("failoverCB")
				.setFallbackUri("forward:/library-failover/")
				.setRouteId("libraryFailover"))
			    **.filter(authFilter)**))
			.uri("lb://library"))
		.route("library_failover_route", r -> r.path("/library-failover/**")
			**.filters(f -> f.filter(authFilter)**)
			.uri("lb://library-failover"))
		.build();
}
```



