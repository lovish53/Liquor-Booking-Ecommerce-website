package com.liquor.liquor.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.liquor.liquor.dto.BrandRequest;
import com.liquor.liquor.dto.BrandResponse;
import com.liquor.liquor.service.BrandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<BrandResponse> addBrand(
            @Valid @RequestBody BrandRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(brandService.addBrand(request));
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {

        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable UUID id,
            @Valid @RequestBody BrandRequest request) {

        return ResponseEntity.ok(
                brandService.updateBrand(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(
            @PathVariable UUID id) {

        brandService.deleteBrand(id);

        return ResponseEntity.ok("Brand deleted successfully");
    }

}
