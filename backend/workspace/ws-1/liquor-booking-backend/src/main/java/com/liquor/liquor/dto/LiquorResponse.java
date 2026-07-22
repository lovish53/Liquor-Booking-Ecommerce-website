package com.liquor.liquor.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.liquor.liquor.entity.BottleSize;

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
public class LiquorResponse {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal purchasePrice;

    private BigDecimal sellingPrice;

    private BigDecimal finalPrice;

    private Integer stock;

    private BottleSize bottleSize;

    private Double alcoholPercentage;

    private String imagePath;

    private Integer discountPercentage;

    private Boolean active;

    private String brandName;

    private String categoryName;

}
