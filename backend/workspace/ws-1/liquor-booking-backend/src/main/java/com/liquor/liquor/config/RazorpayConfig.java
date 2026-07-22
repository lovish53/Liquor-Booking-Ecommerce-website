package com.liquor.liquor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Configuration
public class RazorpayConfig {

    @org.springframework.beans.factory.annotation.Value("${razorpay.key-id}")
    private String keyId;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key-secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient()
            throws RazorpayException {

        return new RazorpayClient(keyId, keySecret);

    }

}
