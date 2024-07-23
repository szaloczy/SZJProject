package com.szj.demo.service;

import com.szj.demo.dtos.order.OrderDTO;
import com.szj.demo.model.Cart;
import com.szj.demo.model.Order;
import com.szj.demo.model.OrderItem;
import com.szj.demo.model.User;
import com.szj.demo.repository.CartItemRepository;
import com.szj.demo.repository.CartRepository;
import com.szj.demo.repository.OrderItemRepository;
import com.szj.demo.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
   public OrderDTO createOrder(User user) {
        Cart cart = cartRepository.findCartByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setOrderDate(LocalDate.now());

        double totalAmount = 0.0;
        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getCartProduct());
            orderItem.setQuantity(cartItem.getCartItemQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).toList();

        for(OrderItem orderItem : orderItems) {
            totalAmount += orderItem.getProduct().getPrice() * orderItem.getQuantity();
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalAmount);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return new OrderDTO(order);
    }
}
