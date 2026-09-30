package com.ak.paymentsapi;

import java.util.Map;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TestController {

	private final BuildProperties buildProperties;

	TestController(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@GetMapping("/test")
	Map<String, String> test() {
		return Map.of("version", buildProperties.getVersion());
	}

	//implement a GET method to get the app name
	@GetMapping("/app-name")
	Map<String, String> appName() {
		return Map.of("name", buildProperties.getName());
	}
}
