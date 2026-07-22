package com.liquor.liquor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.Payment;

public interface PaymentRepository
extends JpaRepository<Payment,UUID>{

    Optional<Payment> findByOrder(Order order);

}
