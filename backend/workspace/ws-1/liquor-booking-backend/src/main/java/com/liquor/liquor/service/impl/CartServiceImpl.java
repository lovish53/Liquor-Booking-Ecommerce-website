package com.liquor.liquor.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.liquor.liquor.dto.CartItemRequest;
import com.liquor.liquor.dto.CartResponse;
import com.liquor.liquor.entity.Cart;
import com.liquor.liquor.entity.CartItem;
import com.liquor.liquor.entity.Liquor;
import com.liquor.liquor.entity.UserEntity;
import com.liquor.liquor.mapper.CartMapper;
import com.liquor.liquor.repository.CartItemRepository;
import com.liquor.liquor.repository.CartRepository;
import com.liquor.liquor.service.CartService;
import com.liquor.liquor.service.LiquorService;
import com.liquor.liquor.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger logger =
            LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final UserService userService;
    private final LiquorService liquorService;

    private final CartMapper cartMapper;
    
    @Override
    public CartResponse addToCart(CartItemRequest request) {

        logger.info("Adding liquor to cart");

        UserEntity user =
                userService.getUserEntity(request.getUserId());

        Cart cart =
                getOrCreateCart(user);

        Liquor liquor =
                liquorService.getLiquorEntity(
                        request.getLiquorId());

        Optional<CartItem> optional =
                cartItemRepository.findByCartAndLiquor(
                        cart,
                        liquor);

        if(optional.isPresent()){

            CartItem cartItem = optional.get();

            cartItem.setQuantity(
                    cartItem.getQuantity()
                            +
                            request.getQuantity());

            cartItemRepository.save(cartItem);

            logger.info("Quantity updated");

        }

        else{

            CartItem cartItem =
                    CartItem.builder()
                            .cart(cart)
                            .liquor(liquor)
                            .quantity(request.getQuantity())
                            .build();

            cartItemRepository.save(cartItem);

            logger.info("New item added");

        }

        return cartMapper.toResponse(cart);

    }
    
    
    private Cart getOrCreateCart(UserEntity user){

        return cartRepository.findByUser(user)
                .orElseGet(() -> {

                    logger.info("Creating cart for user : {}",
                            user.getId());

                    Cart cart =
                            Cart.builder()
                                    .user(user)
                                    .build();

                    return cartRepository.save(cart);

                });

    }
    
    
    @Override
    public CartResponse getCart(UUID userId) {

        logger.info("Fetching cart for user : {}", userId);

        UserEntity user = userService.getUserEntity(userId);

        Cart cart = getOrCreateCart(user);

        return cartMapper.toResponse(cart);

    }
    
    
    @Override
    public CartResponse updateQuantity(UUID cartItemId,
                                       Integer quantity) {

        logger.info("Updating quantity");

        CartItem cartItem = findCartItemById(cartItemId);

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return cartMapper.toResponse(cartItem.getCart());

    }
    
    
    @Override
    public void removeItem(UUID cartItemId) {

        logger.info("Removing item from cart");

        CartItem cartItem =
                findCartItemById(cartItemId);

        cartItemRepository.delete(cartItem);

        logger.info("Item removed successfully");

    }
    
    
    @Override
    public void clearCart(UUID userId) {

        logger.info("Clearing cart");

        UserEntity user =
                userService.getUserEntity(userId);

        Cart cart =
                getOrCreateCart(user);

        cartItemRepository.deleteAll(cart.getCartItems());

        logger.info("Cart cleared successfully");

    }
    
    
    private CartItem findCartItemById(UUID id){

        logger.info("Fetching cart item");

        return cartItemRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Cart item not found");

                    return new RuntimeException("Cart Item not found");

                });

    }

}
