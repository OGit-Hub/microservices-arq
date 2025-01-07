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



