package com.ak.paymentsapi;

import java.util.Map;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Application", description = "Application information endpoints")
class TestController {

	private final BuildProperties buildProperties;

	TestController(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@GetMapping("/test")
	@Operation(summary = "Get the application version")
	Map<String, String> test() {
		return Map.of("version", buildProperties.getVersion());
	}

	@GetMapping("/app-name")
	@Operation(summary = "Get the application name")
	Map<String, String> appName() {
		return Map.of("name", buildProperties.getName());
	}
}
