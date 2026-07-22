package com.liquor.liquor.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.liquor.liquor.repository.CartItemRepository;
import com.liquor.liquor.repository.CartRepository;
import com.liquor.liquor.repository.LiquorRepository;
import com.liquor.liquor.repository.OrderRepository;
import com.liquor.liquor.service.OrderProcessingService;
import com.liquor.liquor.entity.Cart;
import com.liquor.liquor.entity.Liquor;
import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.OrderItem;
import com.liquor.liquor.entity.OrderStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderProcessingServiceImpl
        implements OrderProcessingService {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderProcessingServiceImpl.class);

    private final OrderRepository orderRepository;

    private final LiquorRepository liquorRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    @Override
    public void processSuccessfulPayment(Order order) {

        logger.info("Processing successful payment");

        order.setStatus(OrderStatus.PAID);

        orderRepository.save(order);

        for(OrderItem item : order.getOrderItems()){

            Liquor liquor = item.getLiquor();

            liquor.setStock(

                    liquor.getStock()

                    -

                    item.getQuantity());

            liquorRepository.save(liquor);

        }

        Cart cart =
                cartRepository.findByUser(order.getUser())
                        .orElseThrow(() ->
                                new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());

        logger.info("Order processed successfully");

    }
    
    
    
    
    
}
