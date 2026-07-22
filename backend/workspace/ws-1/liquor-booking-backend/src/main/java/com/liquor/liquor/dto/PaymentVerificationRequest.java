package com.liquor.liquor.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationRequest {
	private UUID orderId;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;
}
