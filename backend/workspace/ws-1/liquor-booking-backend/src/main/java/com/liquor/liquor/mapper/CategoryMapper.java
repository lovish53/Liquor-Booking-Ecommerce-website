package com.liquor.liquor.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.liquor.liquor.dto.CategoryRequest;
import com.liquor.liquor.dto.CategoryResponse;
import com.liquor.liquor.entity.Category;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public Category toEntity(CategoryRequest request) {

        return modelMapper.map(request, Category.class);

    }

    public CategoryResponse toResponse(Category category) {

        CategoryResponse response =
                modelMapper.map(category, CategoryResponse.class);

        response.setTotalBrands(category.getBrands().size());

        return response;
    }

    public void updateEntity(CategoryRequest request, Category category) {

        modelMapper.map(request, category);

    }
}