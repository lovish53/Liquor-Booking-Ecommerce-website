package com.liquor.liquor.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.CheckoutRequest;
import com.liquor.liquor.dto.CheckoutResponse;
import com.liquor.liquor.entity.Cart;
import com.liquor.liquor.entity.CartItem;
import com.liquor.liquor.entity.Order;
import com.liquor.liquor.entity.OrderItem;
import com.liquor.liquor.entity.OrderStatus;
import com.liquor.liquor.entity.Payment;
import com.liquor.liquor.entity.PaymentStatus;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.repository.CartRepository;
import com.liquor.liquor.repository.OrderRepository;
import com.liquor.liquor.repository.PaymentRepository;
import com.liquor.liquor.service.CheckoutService;
import com.liquor.liquor.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

	private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceImpl.class);

	private final UserService userService;

	private final CartRepository cartRepository;

	private final OrderRepository orderRepository;

	private final PaymentRepository paymentRepository;

	@Override
	public CheckoutResponse checkout(CheckoutRequest request) {

		logger.info("Checkout started for user : {}", request.getUserId());

		UserEntity user = getUser(request.getUserId());

		Cart cart = getCart(user);

		validateCart(cart);

		validateStock(cart);

		BigDecimal totalAmount = calculateTotalAmount(cart);

		Order order = createOrder(user, cart, totalAmount);

		Payment payment = createPayment(order);

		logger.info("Checkout completed successfully. Order Id : {}", order.getId());

		return buildResponse(order, payment);
	}

	private UserEntity getUser(UUID userId) {

		logger.info("Fetching user : {}", userId);

		return userService.getUserEntity(userId);

	}

	private Cart getCart(UserEntity user) {

		logger.info("Fetching cart");

		return cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

	}

	private void validateCart(Cart cart) {

		if (cart.getCartItems().isEmpty()) {

			logger.warn("Cart is empty");

			throw new RuntimeException("Cart is empty");
		}

	}

	private void validateStock(Cart cart) {

		logger.info("Validating stock");

		for (CartItem item : cart.getCartItems()) {

			if (item.getQuantity() > item.getLiquor().getStock()) {

				throw new RuntimeException(item.getLiquor().getName() + " is out of stock");
			}

		}

	}

	private BigDecimal calculateTotalAmount(Cart cart) {

		BigDecimal total = BigDecimal.ZERO;

		for (CartItem item : cart.getCartItems()) {

			BigDecimal finalPrice = calculateFinalPrice(item.getLiquor().getSellingPrice(),
					item.getLiquor().getDiscountPercentage());

			total = total.add(finalPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
		}

		return total;

	}

	private BigDecimal calculateFinalPrice(BigDecimal sellingPrice, Integer discountPercentage) {

		if (discountPercentage == null || discountPercentage == 0) {
			return sellingPrice;
		}

		BigDecimal discount = sellingPrice.multiply(BigDecimal.valueOf(discountPercentage))
				.divide(BigDecimal.valueOf(100));

		return sellingPrice.subtract(discount);

	}

	private Order createOrder(UserEntity user, Cart cart, BigDecimal totalAmount) {

		Order order = Order.builder().user(user).totalAmount(totalAmount).status(OrderStatus.PENDING).build();

		for (CartItem cartItem : cart.getCartItems()) {

			OrderItem orderItem = OrderItem.builder().order(order).liquor(cartItem.getLiquor())
					.quantity(cartItem.getQuantity()).price(calculateFinalPrice(cartItem.getLiquor().getSellingPrice(),
							cartItem.getLiquor().getDiscountPercentage()))
					.build();

			order.getOrderItems().add(orderItem);
		}

		return orderRepository.save(order);

	}

	private Payment createPayment(Order order) {

		Payment payment = Payment.builder().order(order).amount(order.getTotalAmount()).status(PaymentStatus.PENDING)
				.build();

		return paymentRepository.save(payment);

	}

	private CheckoutResponse buildResponse(Order order, Payment payment) {

		return CheckoutResponse.builder().orderId(order.getId()).totalAmount(order.getTotalAmount())
				.orderStatus(order.getStatus()).paymentStatus(payment.getStatus()).build();

	}

}
