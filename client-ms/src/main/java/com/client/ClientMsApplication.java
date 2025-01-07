package com.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.client.config.LoadBalancerConfig;

@SpringBootApplication
@EnableFeignClients
@LoadBalancerClient(name="library-ms", configuration = LoadBalancerConfig.class)
/**
 * 
 * If we need multiple client, we can put it like this
 * 
 * @LoadBalancerClients(value = {
   @LoadBalancerClient(value = "my-service", configuration = CustomConfig.class),
   @LoadBalancerClient(value = "my-other-service", configuration = CustomConfig.class)
	})
 */
public class ClientMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientMsApplication.class, args);
	}

}
