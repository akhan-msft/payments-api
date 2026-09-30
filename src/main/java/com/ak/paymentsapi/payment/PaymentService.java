package com.ak.paymentsapi.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Stub implementation backed by an in-memory map; no real payment processing or persistence.
 */
@Service
public class PaymentService {

	private final Map<UUID, Payment> payments = new ConcurrentHashMap<>();

	public Payment create(CreatePaymentRequest request) {
		validate(request);
		Payment payment = new Payment(
				UUID.randomUUID(),
				request.customerId(),
				request.productId(),
				request.amount(),
				request.currency().toUpperCase(Locale.ROOT),
				request.method(),
				PaymentStatus.PENDING,
				Instant.now());
		payments.put(payment.id(), payment);
		return payment;
	}

	public List<Payment> list() {
		return payments.values().stream()
				.sorted(Comparator.comparing(Payment::createdAt))
				.toList();
	}

	public Payment cancel(UUID id) {
		Payment updated = payments.computeIfPresent(id, (key, payment) -> {
			if (payment.status() == PaymentStatus.COMPLETED) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Completed payments cannot be cancelled");
			}
			return payment.withStatus(PaymentStatus.CANCELLED);
		});
		if (updated == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
		}
		return updated;
	}

	private static void validate(CreatePaymentRequest request) {
		if (request == null
				|| isBlank(request.customerId())
				|| isBlank(request.productId())
				|| request.method() == null) {
			throw badRequest("customerId, productId and method are required");
		}
		if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
			throw badRequest("amount must be greater than zero");
		}
		if (request.currency() == null || !request.currency().matches("[A-Za-z]{3}")) {
			throw badRequest("currency must be a 3-letter ISO code");
		}
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private static ResponseStatusException badRequest(String message) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
	}
}
