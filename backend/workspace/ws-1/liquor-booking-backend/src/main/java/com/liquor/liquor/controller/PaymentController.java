package com.liquor.liquor.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liquor.liquor.dto.PaymentVerificationRequest;
import com.liquor.liquor.dto.RazorpayOrderResponse;
import com.liquor.liquor.service.RazorpayService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    @PostMapping("/razorpay/orders/{orderId}")
    public ResponseEntity<RazorpayOrderResponse> createRazorpayOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(razorpayService.createOrder(orderId));
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<String> verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        razorpayService.verifyPayment(request);
        return ResponseEntity.ok("Payment verified successfully");
    }
}
