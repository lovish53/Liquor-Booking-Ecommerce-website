package com.liquor.liquor.service;

import com.liquor.liquor.entity.Order;

public interface OrderProcessingService {

	void processSuccessfulPayment(Order order);

}
