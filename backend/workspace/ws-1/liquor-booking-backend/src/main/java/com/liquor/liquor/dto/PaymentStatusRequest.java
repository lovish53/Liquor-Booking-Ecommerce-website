package com.liquor.liquor.dto;

import com.liquor.liquor.entity.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusRequest {

    @NotNull
    private PaymentStatus status;
}
