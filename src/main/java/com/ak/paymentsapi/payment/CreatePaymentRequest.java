package com.ak.paymentsapi.payment;

import java.math.BigDecimal;

public record CreatePaymentRequest(
		String customerId,
		String productId,
		BigDecimal amount,
		String currency,
		PaymentMethod method) {
}
