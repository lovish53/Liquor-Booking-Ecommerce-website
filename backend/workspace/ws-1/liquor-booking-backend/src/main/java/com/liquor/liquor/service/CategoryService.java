package com.liquor.liquor.service;

import java.util.List;

import java.util.UUID;

import com.liquor.liquor.dto.CategoryRequest;
import com.liquor.liquor.dto.CategoryResponse;
import com.liquor.liquor.entity.Category;

public interface CategoryService {
	   CategoryResponse addCategory(CategoryRequest request);

	    List<CategoryResponse> getAllCategories();

	    CategoryResponse getCategoryById(UUID id);

	    CategoryResponse updateCategory(UUID id,
	                                    CategoryRequest request);

	    void deleteCategory(UUID id);
	    
	    Category getCategoryEntity(UUID id);
}
