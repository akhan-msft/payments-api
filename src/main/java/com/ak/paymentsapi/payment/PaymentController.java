package com.ak.paymentsapi.payment;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

	private final PaymentService paymentService;

	PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	ResponseEntity<Payment> create(@RequestBody CreatePaymentRequest request) {
		Payment payment = paymentService.create(request);
		return ResponseEntity.created(URI.create("/api/payments/" + payment.id())).body(payment);
	}

	@GetMapping
	List<Payment> list() {
		return paymentService.list();
	}

	@PostMapping("/{id}/cancel")
	Payment cancel(@PathVariable UUID id) {
		return paymentService.cancel(id);
	}
}
