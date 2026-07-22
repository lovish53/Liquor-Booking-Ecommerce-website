package com.liquor.liquor.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private UUID cartItemId;

    private UUID liquorId;

    private String liquorName;

    private String brandName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

}
