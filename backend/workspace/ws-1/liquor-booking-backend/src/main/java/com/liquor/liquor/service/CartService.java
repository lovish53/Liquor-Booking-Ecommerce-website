package com.liquor.liquor.service;

import java.util.UUID;

import com.liquor.liquor.dto.CartItemRequest;
import com.liquor.liquor.dto.CartResponse;

public interface CartService {

    CartResponse addToCart(CartItemRequest request);

    CartResponse getCart(UUID userId);

    CartResponse updateQuantity(UUID cartItemId,
                                Integer quantity);

    void removeItem(UUID cartItemId);

    void clearCart(UUID userId);

}
