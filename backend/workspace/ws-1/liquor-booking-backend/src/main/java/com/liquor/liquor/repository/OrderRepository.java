package com.liquor.liquor.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.UserEntity;

public interface OrderRepository
extends JpaRepository<Order, UUID> {

    List<Order> findByUser(UserEntity user);
}
