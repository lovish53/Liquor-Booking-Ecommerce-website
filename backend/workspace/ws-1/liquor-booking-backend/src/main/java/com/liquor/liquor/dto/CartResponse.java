package com.liquor.liquor.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private UUID cartId;

    private UUID userId;

    private List<CartItemResponse> items;

    private Integer totalItems;

    private BigDecimal totalAmount;

}