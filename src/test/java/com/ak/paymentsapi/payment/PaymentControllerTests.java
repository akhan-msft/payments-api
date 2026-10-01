package com.ak.paymentsapi.payment;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTests {

	private static final String VALID_PAYMENT = """
			{"customerId":"cust-1","productId":"sku-42","amount":49.99,"currency":"usd","method":"CARD"}
			""";

	@Autowired
	private MockMvc mockMvc;

	private ListAppender<ILoggingEvent> logAppender;

	@BeforeEach
	void captureControllerLogs() {
		Logger logger = (Logger) LoggerFactory.getLogger(PaymentController.class);
		logAppender = new ListAppender<>();
		logAppender.start();
		logger.addAppender(logAppender);
	}

	@AfterEach
	void stopCapturingControllerLogs() {
		Logger logger = (Logger) LoggerFactory.getLogger(PaymentController.class);
		logger.detachAppender(logAppender);
		logAppender.stop();
	}

	@Test
	void createListAndCancelPayment() throws Exception {
		String body = mockMvc.perform(post("/api/payments")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_PAYMENT))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", startsWith("/api/payments/")))
				.andExpect(jsonPath("$.status").value("PENDING"))
				.andExpect(jsonPath("$.currency").value("USD"))
				.andExpect(jsonPath("$.amount").value(49.99))
				.andReturn().getResponse().getContentAsString();
		String id = JsonPath.read(body, "$.id");

		mockMvc.perform(get("/api/payments"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", hasItem(id)));

		mockMvc.perform(post("/api/payments/{id}/cancel", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.status").value("CANCELLED"));
	}

	@Test
	void createRejectsInvalidAmount() throws Exception {
		mockMvc.perform(post("/api/payments")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_PAYMENT.replace("49.99", "0")))
				.andExpect(status().isBadRequest());
	}

	@Test
	void cancelUnknownPaymentReturnsNotFound() throws Exception {
		mockMvc.perform(post("/api/payments/{id}/cancel", UUID.randomUUID()))
				.andExpect(status().isNotFound());
	}

	@Test
	void logsOperationalMetadataWithoutSensitivePaymentData() throws Exception {
		mockMvc.perform(post("/api/payments")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_PAYMENT))
				.andExpect(status().isCreated());

		String logs = logAppender.list.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.reduce("", (all, message) -> all + message);

		org.assertj.core.api.Assertions.assertThat(logs)
				.contains("Payment created:")
				.doesNotContain("cust-1", "sku-42", "49.99", "USD", "CARD");
	}

	@Test
	void logsFailuresWithoutSensitivePaymentData() throws Exception {
		mockMvc.perform(post("/api/payments")
						.contentType(MediaType.APPLICATION_JSON)
						.content(VALID_PAYMENT.replace("49.99", "0")))
				.andExpect(status().isBadRequest());

		String logs = logAppender.list.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.reduce("", (all, message) -> all + message);

		org.assertj.core.api.Assertions.assertThat(logs)
				.contains("Payment creation failed:", "status=400 BAD_REQUEST")
				.doesNotContain("cust-1", "sku-42", "49.99", "USD", "CARD");
	}
}
