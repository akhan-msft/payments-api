package com.ak.paymentsapi.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Payment(
		UUID id,
		String customerId,
		String productId,
		BigDecimal amount,
		String currency,
		PaymentMethod method,
		PaymentStatus status,
		Instant createdAt) {

	Payment withStatus(PaymentStatus newStatus) {
		return new Payment(id, customerId, productId, amount, currency, method, newStatus, createdAt);
	}
}
