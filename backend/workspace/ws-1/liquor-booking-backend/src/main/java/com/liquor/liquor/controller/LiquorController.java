package com.liquor.liquor.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.liquor.liquor.dto.LiquorRequest;
import com.liquor.liquor.dto.LiquorResponse;
import com.liquor.liquor.service.LiquorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/liquors")
@RequiredArgsConstructor
public class LiquorController {

    private final LiquorService liquorService;

    @PostMapping
    public ResponseEntity<LiquorResponse> addLiquor(
            @Valid @RequestBody LiquorRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(liquorService.addLiquor(request));
    }

    @GetMapping
    public ResponseEntity<List<LiquorResponse>> getAllLiquors() {

        return ResponseEntity.ok(liquorService.getAllLiquors());

    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquorResponse> getLiquorById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(liquorService.getLiquorById(id));

    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquorResponse> updateLiquor(
            @PathVariable UUID id,
            @Valid @RequestBody LiquorRequest request) {

        return ResponseEntity.ok(
                liquorService.updateLiquor(id, request));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLiquor(
            @PathVariable UUID id) {

        liquorService.deleteLiquor(id);

        return ResponseEntity.ok("Liquor deleted successfully");

    }

}