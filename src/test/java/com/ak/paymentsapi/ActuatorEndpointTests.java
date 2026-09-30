package com.ak.paymentsapi;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorEndpointTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void healthAndProbeGroupsAreAvailable() throws Exception {
		for (String path : new String[] {
				"/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness"
		}) {
			mockMvc.perform(get(path))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.status").value("UP"));
		}
	}

	@Test
	void onlyPaymentInvocationMetricsAreQueryable() throws Exception {
		mockMvc.perform(get("/api/payments")).andExpect(status().isOk());
		mockMvc.perform(post("/api/payments/{id}/cancel", UUID.randomUUID()))
				.andExpect(status().isNotFound());
		mockMvc.perform(get("/test")).andExpect(status().isOk());

		mockMvc.perform(get("/actuator/metrics"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.names", contains("http.server.requests")));
		mockMvc.perform(get("/actuator/metrics/http.server.requests")
						.param("tag", "uri:/api/payments"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.measurements[0].value", greaterThanOrEqualTo(1.0)));
		mockMvc.perform(get("/actuator/metrics/http.server.requests")
						.param("tag", "uri:/api/payments/{id}/cancel"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.measurements[0].value", greaterThanOrEqualTo(1.0)));
		mockMvc.perform(get("/actuator/metrics/http.server.requests")
						.param("tag", "uri:/test"))
				.andExpect(status().isNotFound());
		mockMvc.perform(get("/actuator/metrics/jvm.memory.used"))
				.andExpect(status().isNotFound());
	}

	@Test
	void allOtherActuatorEndpointsAreUnavailable() throws Exception {
		for (String path : new String[] {
				"/actuator/info", "/actuator/env", "/actuator/beans",
				"/actuator/loggers", "/actuator/prometheus"
		}) {
			mockMvc.perform(get(path)).andExpect(status().isNotFound());
		}
		mockMvc.perform(get("/actuator"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._links", aMapWithSize(5)))
				.andExpect(jsonPath("$._links", hasKey("self")))
				.andExpect(jsonPath("$._links", hasKey("health")))
				.andExpect(jsonPath("$._links", hasKey("health-path")))
				.andExpect(jsonPath("$._links", hasKey("metrics")))
				.andExpect(jsonPath("$._links", hasKey("metrics-requiredMetricName")));
	}
}
