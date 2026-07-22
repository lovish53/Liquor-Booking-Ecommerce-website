package com.liquor.liquor.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {

    private UUID id;

    private String name;

    private String description;

    private Boolean active;

    private String categoryName;

    private Integer totalLiquors;

}
