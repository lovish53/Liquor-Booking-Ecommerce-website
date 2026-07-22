package com.liquor.liquor.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liquor.liquor.dto.CategoryRequest;
import com.liquor.liquor.dto.CategoryResponse;
import com.liquor.liquor.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryService categoryService;
	
	@PostMapping
	public ResponseEntity<CategoryResponse> addCategory(
	        @Valid @RequestBody CategoryRequest request){

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(categoryService.addCategory(request));

	}
	
	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAllCategories(){

	    return ResponseEntity.ok(
	            categoryService.getAllCategories());

	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponse> getCategoryById(
	        @PathVariable UUID id){

	    return ResponseEntity.ok(
	            categoryService.getCategoryById(id));

	}
	
	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponse> updateCategory(
	        @PathVariable UUID id,
	        @Valid @RequestBody CategoryRequest request){

	    return ResponseEntity.ok(
	            categoryService.updateCategory(id,request));

	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCategory(
	        @PathVariable UUID id){

	    categoryService.deleteCategory(id);

	    return ResponseEntity.ok("Category deleted successfully");

	}
	
	
}
