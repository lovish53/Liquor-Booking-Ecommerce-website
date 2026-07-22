package com.liquor.liquor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
public class OrderResponse {

    private UUID id;

    private UUID userId;

    private String customerName;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}
