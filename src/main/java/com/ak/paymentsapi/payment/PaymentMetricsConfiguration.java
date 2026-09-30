package com.ak.paymentsapi.payment;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class PaymentMetricsConfiguration {

	@Bean
	MeterFilter paymentInvocationMetricsOnly() {
		return MeterFilter.denyUnless(id -> {
			if (!"http.server.requests".equals(id.getName())) {
				return false;
			}
			String uri = id.getTag("uri");
			return "/api/payments".equals(uri) || (uri != null && uri.startsWith("/api/payments/"));
		});
	}
}
