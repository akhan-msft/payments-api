package com.ak.paymentsapi.payment;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	private final PaymentService paymentService;

	PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	ResponseEntity<Payment> create(@RequestBody CreatePaymentRequest request) {
		logger.info("Creating payment");
		Payment payment = paymentService.create(request);
		logger.info("Payment created");
		return ResponseEntity.created(URI.create("/api/payments/" + payment.id())).body(payment);
	}

	@GetMapping
	List<Payment> list() {
		logger.info("Listing payments");
		List<Payment> payments = paymentService.list();
		logger.info("Listed {} payments", payments.size());
		return payments;
	}

	@PostMapping("/{id}/cancel")
	Payment cancel(@PathVariable UUID id) {
		logger.info("Cancelling payment");
		Payment payment = paymentService.cancel(id);
		logger.info("Payment cancelled");
		return payment;
	}
}
