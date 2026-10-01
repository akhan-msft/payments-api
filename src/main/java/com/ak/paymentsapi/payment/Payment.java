package com.ak.paymentsapi.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable representation of a payment returned by the payments API.
 *
 * @param id unique payment identifier
 * @param customerId identifier of the customer who made the payment
 * @param productId identifier of the purchased product
 * @param amount payment amount
 * @param currency three-letter ISO currency code
 * @param method method used to make the payment
 * @param status current payment status
 * @param createdAt time the payment was created
 */
public record Payment(
		UUID id,
		String customerId,
		String productId,
		BigDecimal amount,
		String currency,
		PaymentMethod method,
		PaymentStatus status,
		Instant createdAt) {

	/**
	 * Returns a copy of this payment with a new lifecycle status.
	 *
	 * @param newStatus status to assign to the copy
	 * @return a payment retaining all fields except its status
	 */
	Payment withStatus(PaymentStatus newStatus) {
		return new Payment(id, customerId, productId, amount, currency, method, newStatus, createdAt);
	}
}
