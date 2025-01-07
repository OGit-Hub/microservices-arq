# Eureka Client Discovery communication - Feign

To make calls between microservices, we can use both RestTemplate or Feign through eureka service discovery client. This time we will use the Feign interface.

To begin, we will create a project with the previously mentioned libraries and we will also add the `spring-cloud-starter-openfeign` library that will allow us to configure the clients.

Once we have created the project, we will add the following annotation `@EnableFeignClients` to the main class

```
@SpringBootApplication
@EnableFeignClients
public class ClientMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientMsApplication.class, args);
	}

}

```

Afterwards, we create an interface to make the calls to the microservice that we need. In this interface you will have to add the annotation `@FeignClient`, indicating the name of the microservice with which it is registered in Eureka.

```

@FeignClient("library")
public interface LibraryClient {
	
	@GetMapping("/library")
	public ResponseEntity<List<Book>> getLibrary();

}
```

For each method, we will add the annotation indicating the HTTP type of the request, as well as the full path (the `@RequestMapping` of the controller must be included)

### Client Side load balancing

By default, calls made with a `@FeignClient` will have a Round-Robin balancing configuration (it will change between instances of the same microservice with each request).

This can be modified by adding the annotation `@LoadBalancerClient` and indicating a configuration class for it in the main class.

```
@SpringBootApplication
@EnableFeignClients
@LoadBalancerClient(name="library", configuration = LoadBalancerConfig.class)
public class ClientMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientMsApplication.class, args);
	}

}

....

@Configuration
public class LoadBalancerConfig {

    @Bean
    ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withBlockingDiscoveryClient()
                .withSameInstancePreference()
                .build(context);
    }

}
```

In the example shown above, we are changing the balancing method for one that prioritizes the same instance

> [!NOTE]
> Depending on the version of Spring we are using, the annotation `@LoadBalancerClient` could be added in the same interface in which the client was defined. In the version we use, no.

To test this, we have created several instances in STS of the **library** microservice and made 10 consecutive calls through a rest API in the **client** microservice
```
@RestController
@RequestMapping("/client")

public class Controller {
	
	@Autowired
	LibraryClient libraryClient;
	
	@GetMapping("/consultLibrary")
	public ResponseEntity<List<Book>> consultCurrentLibrary(){
		
		IntStream.range(1, 10).forEach(value -> libraryClient.getLibrary());
		
		return libraryClient.getLibrary();
	}

}
```

We can see then in the **library** microservice log than only one of the instance is getting the requests (we could test Round Robin behavior by default to see that request are rotating between instances).

> [!NOTE]
> If `@PatchMapping` is needed, we will need to do a workaround cauise Feign does not support it.
>
> First, we will need to add the next dependecy to our project
>
> ```
> <dependency>
> 	<groupId>io.github.openfeign</groupId>
> 	<artifactId>feign-okhttp</artifactId>
> </dependency>
> ```
>
> Then, we will need to add a `@Bean` in a configuration class, let say `FeignConfig.class`
>
> ```
> @Configuration
> public class FeignConfig {
>	
>    @Bean
>    public OkHttpClient client() {
>      return new OkHttpClient();
>    }
>
> }
> ```

> [!WARNING]  
> Aparently the above solution will prevent eureka service discovery from working so we will have to indicate the url in the feign client or use other approach like Rest Client.

