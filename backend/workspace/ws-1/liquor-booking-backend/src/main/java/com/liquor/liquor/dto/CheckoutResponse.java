package com.liquor.liquor.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.liquor.liquor.entity.OrderStatus;
import com.liquor.liquor.entity.PaymentStatus;

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
public class CheckoutResponse {
	private UUID orderId;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;
}
