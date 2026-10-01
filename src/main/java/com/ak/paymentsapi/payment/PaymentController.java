package com.ak.paymentsapi.payment;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
class PaymentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

	private final PaymentService paymentService;

	PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	ResponseEntity<Payment> create(@RequestBody CreatePaymentRequest request) {
		try {
			Payment payment = paymentService.create(request);
			LOGGER.info("Payment created: id={}, status={}", payment.id(), payment.status());
			return ResponseEntity.created(URI.create("/api/payments/" + payment.id())).body(payment);
		} catch (ResponseStatusException exception) {
			LOGGER.warn("Payment creation failed: status={}", exception.getStatusCode());
			throw exception;
		}
	}

	@GetMapping
	List<Payment> list() {
		List<Payment> payments = paymentService.list();
		LOGGER.info("Payments listed: count={}", payments.size());
		return payments;
	}

	@PostMapping("/{id}/cancel")
	Payment cancel(@PathVariable UUID id) {
		try {
			Payment payment = paymentService.cancel(id);
			LOGGER.info("Payment cancelled: id={}, status={}", payment.id(), payment.status());
			return payment;
		} catch (ResponseStatusException exception) {
			LOGGER.warn("Payment cancellation failed: id={}, status={}", id, exception.getStatusCode());
			throw exception;
		}
	}
}
