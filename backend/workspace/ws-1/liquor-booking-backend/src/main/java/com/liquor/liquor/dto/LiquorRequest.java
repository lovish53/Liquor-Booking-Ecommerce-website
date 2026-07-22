package com.liquor.liquor.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.liquor.liquor.entity.BottleSize;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class LiquorRequest {

    @NotBlank(message = "Liquor name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal sellingPrice;

    @NotNull(message = "Stock is required")
    @Min(0)
    private Integer stock;

    @NotNull(message = "Bottle size is required")
    private BottleSize bottleSize;

    @NotNull(message = "Alcohol percentage is required")
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double alcoholPercentage;

    private String imagePath;

    @Min(0)
    @Max(100)
    private Integer discountPercentage;

    @NotNull(message = "Brand is required")
    private UUID brandId;

}
