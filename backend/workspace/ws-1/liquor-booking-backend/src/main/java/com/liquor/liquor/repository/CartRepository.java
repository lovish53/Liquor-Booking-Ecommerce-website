package com.liquor.liquor.repository;

import java.util.Optional;
import java.util.UUID;


import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.Cart;
import com.liquor.liquor.entity.UserEntity;

public interface CartRepository extends JpaRepository<Cart, UUID>{

    Optional<Cart> findByUser(UserEntity user);

}
