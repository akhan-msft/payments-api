package com.ak.paymentsapi.payment;

import java.math.BigDecimal;

/**
 * Client-supplied details required to create a payment.
 *
 * @param customerId identifier of the customer making the payment
 * @param productId identifier of the product being purchased
 * @param amount positive payment amount
 * @param currency three-letter ISO currency code
 * @param method method to use for the payment
 */
public record CreatePaymentRequest(
		String customerId,
		String productId,
		BigDecimal amount,
		String currency,
		PaymentMethod method) {
}
