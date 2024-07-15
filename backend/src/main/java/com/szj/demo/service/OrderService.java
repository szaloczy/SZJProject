package com.szj.demo.service;

import com.szj.demo.model.Cart;
import com.szj.demo.model.Order;
import com.szj.demo.model.User;
import com.szj.demo.repository.CartItemRepository;
import com.szj.demo.repository.CartRepository;
import com.szj.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    /*
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;*/

   /*public Order createOrder(User user) {
        Cart cart = cartRepository.findCartByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }*/
}
