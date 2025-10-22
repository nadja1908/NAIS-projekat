package rs.ac.uns.acs.nais.RelationalDatabaseService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

@SpringBootApplication
@EnableDiscoveryClient
public class RelationalDatabaseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelationalDatabaseServiceApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	// Plain RestTemplate (no load balancing) for direct container/host calls
	@Bean
	public RestTemplate simpleRestTemplate() {
		return new RestTemplate();
	}

}
