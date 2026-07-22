package com.liquor.liquor.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.liquor.liquor.dto.CartItemResponse;
import com.liquor.liquor.dto.CartResponse;
import com.liquor.liquor.entity.Cart;
import com.liquor.liquor.entity.CartItem;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items = cart.getCartItems()
                .stream()
                .map(this::mapCartItem)
                .toList();

        Integer totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();

    }

    private CartItemResponse mapCartItem(CartItem cartItem) {

        BigDecimal price =
                calculateFinalPrice(
                        cartItem.getLiquor().getSellingPrice(),
                        cartItem.getLiquor().getDiscountPercentage());

        BigDecimal totalPrice =
                price.multiply(
                        BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .liquorId(cartItem.getLiquor().getId())
                .liquorName(cartItem.getLiquor().getName())
                .brandName(cartItem.getLiquor().getBrand().getName())
                .quantity(cartItem.getQuantity())
                .price(price)
                .totalPrice(totalPrice)
                .build();

    }

    private BigDecimal calculateFinalPrice(
            BigDecimal sellingPrice,
            Integer discountPercentage) {

        if (discountPercentage == null || discountPercentage == 0) {
            return sellingPrice;
        }

        BigDecimal discount = sellingPrice
                .multiply(BigDecimal.valueOf(discountPercentage))
                .divide(BigDecimal.valueOf(100));

        return sellingPrice.subtract(discount);

    }

}
