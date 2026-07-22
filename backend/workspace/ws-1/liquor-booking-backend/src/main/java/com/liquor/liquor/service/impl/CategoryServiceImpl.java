package com.liquor.liquor.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.CategoryRequest;
import com.liquor.liquor.dto.CategoryResponse;
import com.liquor.liquor.entity.Category;
import com.liquor.liquor.mapper.CategoryMapper;
import com.liquor.liquor.repository.CategoryRepository;
import com.liquor.liquor.service.CategoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger =
            LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {

        logger.info("Adding category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {

            logger.warn("Category already exists: {}", request.getName());

            throw new RuntimeException("Category already exists");
        }

        Category category = categoryMapper.toEntity(request);

        category.setActive(true);

        category = categoryRepository.save(category);

        logger.info("Category created successfully. Category Id: {}", category.getId());

        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        logger.info("Fetching all categories");

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {

        Category category = findCategoryById(id);

        logger.info("Category fetched successfully. Category Id: {}", id);

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {

        logger.info("Updating category with id: {}", id);

        Category category = findCategoryById(id);

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByName(request.getName())) {

            logger.warn("Category name already exists: {}", request.getName());

            throw new RuntimeException("Category already exists");
        }

        categoryMapper.updateEntity(request, category);

        category = categoryRepository.save(category);

        logger.info("Category updated successfully. Category Id: {}", category.getId());

        return categoryMapper.toResponse(category);
    }

    @Override
    public void deleteCategory(UUID id) {

        logger.info("Deleting category with id: {}", id);

        Category category = findCategoryById(id);

        categoryRepository.delete(category);

        logger.info("Category deleted successfully. Category Id: {}", id);
    }

    private Category findCategoryById(UUID id) {

        logger.info("Fetching category with id: {}", id);

        return categoryRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Category not found with id: {}", id);

                    return new RuntimeException("Category not found");
                });
    }
    @Override
    public Category getCategoryEntity(UUID id) {

        return findCategoryById(id);

    }
   
}
