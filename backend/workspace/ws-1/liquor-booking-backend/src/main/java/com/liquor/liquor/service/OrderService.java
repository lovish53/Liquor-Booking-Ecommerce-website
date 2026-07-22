package com.liquor.liquor.service;

import java.util.List;
import java.util.UUID;

import com.liquor.liquor.dto.OrderResponse;
import com.liquor.liquor.entity.OrderStatus;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByUser(UUID userId);

    OrderResponse getOrderById(UUID id);

    OrderResponse updateStatus(UUID id, OrderStatus status);
}
