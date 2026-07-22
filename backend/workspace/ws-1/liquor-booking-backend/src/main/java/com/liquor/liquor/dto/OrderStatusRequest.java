package com.liquor.liquor.dto;

import com.liquor.liquor.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {

    @NotNull
    private OrderStatus status;
}
