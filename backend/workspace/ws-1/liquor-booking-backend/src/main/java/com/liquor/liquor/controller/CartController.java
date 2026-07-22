package com.liquor.liquor.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquor.liquor.dto.CartItemRequest;
import com.liquor.liquor.dto.CartResponse;
import com.liquor.liquor.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody CartItemRequest request){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCart(request));

    }
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable UUID userId){

        return ResponseEntity.ok(
                cartService.getCart(userId));

    }
    
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable UUID cartItemId,
            @RequestParam Integer quantity){

        return ResponseEntity.ok(
                cartService.updateQuantity(cartItemId,
                        quantity));

    }
    
    
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<String> removeItem(
            @PathVariable UUID cartItemId){

        cartService.removeItem(cartItemId);

        return ResponseEntity.ok("Item removed successfully");

    }
    
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> clearCart(
            @PathVariable UUID userId){

        cartService.clearCart(userId);

        return ResponseEntity.ok("Cart cleared successfully");

    }
    
    
    
    
    
    
    

}
