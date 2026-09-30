package com.ak.paymentsapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BuildProperties buildProperties;

	@Test
	void testReturnsProjectVersion() throws Exception {
		mockMvc.perform(get("/test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.version").value(buildProperties.getVersion()));
	}

	@Test
	void actuatorHealthIsAvailable() throws Exception {
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void openApiSpecificationIncludesApiMetadataAndEndpoints() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.openapi", Matchers.startsWith("3.")))
				.andExpect(jsonPath("$.info.title").value("Payments API"))
				.andExpect(jsonPath("$.info.description").value("REST API for the payments service."))
				.andExpect(jsonPath("$.info.version").value(buildProperties.getVersion()))
				.andExpect(jsonPath("$.paths['/test'].get.summary").value("Get the application version"))
				.andExpect(jsonPath("$.paths['/app-name'].get.summary").value("Get the application name"));
	}

	@Test
	void swaggerUiIsAvailableAtDocsPath() throws Exception {
		mockMvc.perform(get("/docs"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string("Location", Matchers.containsString("swagger-ui/index.html")));

		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"));
	}
}
