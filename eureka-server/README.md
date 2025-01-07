# Eureka server - Service Discovery Client
On this occasion, we are going to create a project with Spring Boot to be able to build a eureka server be able to register and locate our multiple microservices

When creating the project using Spring Starter, we will need to select the `spring-boot-devtools` and `spring-cloud-starter-netflix-eureka-server` libraries (We could also add Configuration Server related libraries commented on {enlace})

Once the project is created, so that when the application starts it does so as a eureka server, the annotation `@EnableEurekaServer` will have to be added to the main class

```
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
```

### Eureka Server - application.properties

Now, we will need to add some more configuration in order to make the application work.

```

spring.config.import=optional:configserver:http://localhost:8888/
management.endpoints.web.exposure.include=refresh

server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

- `server.port` : This is used to indicate that our server port will be 8761, which is Eureka Server port by default (so it is not needed).
* `eureka.client.register-with-eureka` : This is used to indicate if we want to register on the eureka server. Being the server itself, it makes no sense
+ `eureka.client.fetch-registry` : This is used to indicate if we want to fetch other microservices in eureka server. Eureka Serever itself dont need to do that (only other microservices as a eureka server client).

### Eureka Client Discovery - Microservices

Now, for our microservices to register with the eureka server, it will only be necessary to add the `spring-cloud-starter-netflix-eureka-server` library (as long as the eureka server is on the default port).

If we access our eureka server (http://localhost:8761/) we can see that the service appears registered with the name that we have given in the property `spring.application.name`


