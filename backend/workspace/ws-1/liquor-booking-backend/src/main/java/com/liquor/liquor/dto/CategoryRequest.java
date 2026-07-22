package com.liquor.liquor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
	   @NotBlank(message = "Category name is required")
	    @Size(max = 100)
	    private String name;

	    @Size(max = 300)
	    private String description;
}
