package com.liquor.liquor.service;

import java.util.UUID;

import com.liquor.liquor.dto.PaymentVerificationRequest;
import com.liquor.liquor.dto.RazorpayOrderResponse;

public interface RazorpayService {

    RazorpayOrderResponse createOrder(UUID orderId);

    void verifyPayment(PaymentVerificationRequest request);

}
