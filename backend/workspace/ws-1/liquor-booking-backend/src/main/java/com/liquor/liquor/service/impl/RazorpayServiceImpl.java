package com.liquor.liquor.service.impl;

import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.PaymentVerificationRequest;
import com.liquor.liquor.dto.RazorpayOrderResponse;
import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.Payment;
import com.liquor.liquor.entity.PaymentStatus;
import com.liquor.liquor.repository.OrderRepository;
import com.liquor.liquor.repository.PaymentRepository;
import com.liquor.liquor.service.OrderProcessingService;
import com.liquor.liquor.service.RazorpayService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {
	private final OrderProcessingService orderProcessingService;
    private final RazorpayClient razorpayClient;

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;
    
    @Override
    public RazorpayOrderResponse createOrder(UUID orderId) {
        if (keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) {
            throw new RuntimeException("Razorpay key id/secret are not configured");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getTotalAmount() == null || order.getTotalAmount().signum() <= 0) {
            throw new RuntimeException("Order amount must be greater than zero");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        int amountInPaise = order.getTotalAmount()
                .multiply(java.math.BigDecimal.valueOf(100))
                .intValue();

        if (amountInPaise < 1000) {
            throw new RuntimeException(
                    "Razorpay requires the order total to be at least INR 10.00. Increase quantity or update item prices before payment.");
        }

        if (payment.getRazorpayOrderId() != null && !payment.getRazorpayOrderId().isBlank()) {
            return RazorpayOrderResponse.builder()
                    .orderId(order.getId())
                    .razorpayOrderId(payment.getRazorpayOrderId())
                    .amount(amountInPaise)
                    .currency("INR")
                    .key(keyId)
                    .build();
        }

        try {

            JSONObject request = new JSONObject();

            request.put("amount", amountInPaise);

            request.put("currency", "INR");

            request.put("receipt", order.getId().toString());

            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(request);

            payment.setRazorpayOrderId(
                    razorpayOrder.get("id"));

            paymentRepository.save(payment);

            return RazorpayOrderResponse.builder()
                    .orderId(order.getId())
                    .razorpayOrderId(
                            razorpayOrder.get("id"))
                    .amount(
                            razorpayOrder.get("amount"))
                    .currency(
                            razorpayOrder.get("currency"))
                    .key(keyId)
                    .build();

        }

        catch (RazorpayException e) {

            throw new RuntimeException("Razorpay order creation failed: " + e.getMessage());

        }
        catch (Exception e) {

            throw new RuntimeException("Razorpay order creation failed. Check the order amount and Razorpay credentials. Details: " + e.getMessage());

        }

    }
    @Override
    public void verifyPayment(
            PaymentVerificationRequest request) {

        Order order =
                orderRepository.findById(
                        request.getOrderId())
                        .orElseThrow(() ->
                                new RuntimeException("Order not found"));

        Payment payment =
                paymentRepository.findByOrder(order)
                        .orElseThrow(() ->
                                new RuntimeException("Payment not found"));

        if (!isValidSignature(request)) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Invalid payment signature");
        }

        payment.setRazorpayOrderId(request.getRazorpayOrderId());
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        orderProcessingService.processSuccessfulPayment(order);
    }

    private boolean isValidSignature(PaymentVerificationRequest request) {
        if (request.getRazorpayOrderId() == null
                || request.getRazorpayPaymentId() == null
                || request.getRazorpaySignature() == null) {
            throw new RuntimeException("Razorpay verification payload is incomplete");
        }

        try {
            String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes());
            StringBuilder generatedSignature = new StringBuilder();
            for (byte b : digest) {
                generatedSignature.append(String.format("%02x", b));
            }
            return generatedSignature.toString().equals(request.getRazorpaySignature());
        } catch (Exception exception) {
            throw new RuntimeException("Unable to verify payment");
        }
    }
    
    
}
