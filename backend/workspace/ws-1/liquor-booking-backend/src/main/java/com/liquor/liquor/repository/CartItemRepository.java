package com.liquor.liquor.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.Cart;
import com.liquor.liquor.entity.CartItem;
import com.liquor.liquor.entity.Liquor;

public interface CartItemRepository extends JpaRepository<CartItem, UUID>{

    Optional<CartItem> findByCartAndLiquor(Cart cart,
                                           Liquor liquor);

    List<CartItem> findByCart(Cart cart);

}