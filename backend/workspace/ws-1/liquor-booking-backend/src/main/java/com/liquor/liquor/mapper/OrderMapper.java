package com.liquor.liquor.mapper;

import org.springframework.stereotype.Component;

import com.liquor.liquor.dto.OrderItemResponse;
import com.liquor.liquor.dto.OrderResponse;
import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.OrderItem;
import com.liquor.liquor.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final PaymentRepository paymentRepository;

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .customerName(order.getUser().getName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(paymentRepository.findByOrder(order)
                        .map(payment -> payment.getStatus())
                        .orElse(null))
                .createdAt(order.getCreatedAt())
                .items(order.getOrderItems().stream()
                        .map(this::toItemResponse)
                        .toList())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .liquorId(item.getLiquor().getId())
                .liquorName(item.getLiquor().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}
