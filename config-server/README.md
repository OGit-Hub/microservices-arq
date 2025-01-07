# Externalize configuration using Spring Cloud Config Server
On this occasion, we are going to create a project with Spring Boot to be able to build a configuration server and thus not only be able to access the application configuration externally but also be able to update it without having to stop the applications involved.

When creating the project using Spring Starter, we will need to select the `spring-boot-devtools` and `spring-cloud-config-server` libraries

Once the project is created, so that when the application starts it does so as a configuration server, the annotation `@EnableConfigServer` will have to be added to the main class

```
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
```

### Configuration Server Properties - application.properties

Now, we will need to configure the server in which our properties files will be

```
spring.cloud.config.server.default-label=main
spring.cloud.config.server.git.uri=https://github.com/OGit-Hub/config-server.git
spring.cloud.config.server.git.clone-on-start=true

```


So that our applications (in this case microservices) can use this server, we will need to make the following changes

The following will have to be added to the project libraries:
- `spring-cloud-starter-config` : This library contains both `spring-cloud-config-server` and `spring-cloud-config-client` library. In this case, we will need `spring-cloud-config-client` to connect to the server we just have created
* `spring-boot-starter-actuator` : This library will allow us to update the configuration without having to stop the application
+ `spring-boot-configuration-processor` : Recommended metadata releated library


After adding the libraries, in order to start our application we must configure the server in the corresponding properties file

### Configuration Server Client Properties - application.properties
```
spring.config.import=optional:configserver:http://localhost:8888/
management.endpoints.web.exposure.include=refresh
```
- `spring.config.import` : In this property we indicate where our configuration server is located
* `management.endpoints.web.exposure.include` : By Default, actuator library only exposes `/actuator` endpoint (which shows you available endpoints). We will need `refresh/` to update configuration.


Next, we will need to create a `@Bean` that we will use to collect the properties found on our configuration server

### Configuration Server Client - Bean
```
@Configuration
@ConfigurationProperties
@RefreshScope
public class ConfigServer {
	
	@Value("${application.name}")
	private String applicationName;

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
}
```

Finally, we can use this `@Bean` by injecting the dependency where we need it.

```
@Autowired
private ConfigServer configServer;

......

@GetMapping("/name")
public ResponseEntity<String> getApplicationName() {
	return ResponseEntity.ok(configServer.getApplicationName());
}
```

> [!NOTE]
> It must be taken into account that the name of the properties files that the application expects to find on the configuration server are formed using `spring.application.name`-`spring.profiles.active`. Example. Having this configuration
> ```
> spring.application.name=library
> spring.profiles.active=dev
> ```
> It will expect to find `library-dev.properties`
>
> In addition, it will also take the `application-{spring.profile.active}` file. In case the spring profile is not found, it will take the default one. **Example** application.properties.
> 
> If file name is changed in repository, microservices will not be able to refresh configuration until the file get back to original name or application is reboot.
> 


### Configuration Server Client - Refresh properties

Now that we have the configuration server in our application, if we make a change to the server configuration, we will see that the change is not reflected.

To do this, we will need to use the actuator library

To update the configuration, it will only be necessary to make a `POST` call to the `/actuator/refresh` endpoint



