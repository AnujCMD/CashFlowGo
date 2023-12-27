package io.products.CashFlowGo;

import io.products.CashFlowGo.repository.MongoRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackageClasses = MongoRepository.class)
@OpenAPIDefinition(
		info = @Info(
				title = "CashFlowGo Application",
				version = "1.0.0",
				description = "Application which records the transactions and show it up to the user"
		)
)
public class CashFlowGoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashFlowGoApplication.class, args);
	}
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

}
