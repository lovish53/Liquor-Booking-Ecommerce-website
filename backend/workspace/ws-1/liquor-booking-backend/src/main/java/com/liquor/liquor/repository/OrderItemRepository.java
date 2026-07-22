package com.liquor.liquor.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.OrderItem;

public interface OrderItemRepository
extends JpaRepository<OrderItem, UUID> {
}


