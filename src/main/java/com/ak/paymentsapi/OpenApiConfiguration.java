package com.ak.paymentsapi;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
class OpenApiConfiguration {

	@Bean
	OpenAPI paymentsApiOpenAPI(BuildProperties buildProperties) {
		return new OpenAPI()
				.info(new Info()
						.title("Payments API")
						.description("REST API for the payments service.")
						.version(buildProperties.getVersion()));
	}
}
