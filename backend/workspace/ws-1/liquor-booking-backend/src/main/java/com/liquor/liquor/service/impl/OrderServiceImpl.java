package com.liquor.liquor.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.OrderResponse;
import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.OrderStatus;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.mapper.OrderMapper;
import com.liquor.liquor.repository.OrderRepository;
import com.liquor.liquor.service.OrderService;
import com.liquor.liquor.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserService userService;

    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrdersByUser(UUID userId) {
        UserEntity user = userService.getUserEntity(userId);
        return orderRepository.findByUser(user)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        return orderMapper.toResponse(findById(id));
    }

    @Override
    public OrderResponse updateStatus(UUID id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
