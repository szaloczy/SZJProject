package com.szj.demo.service;

import com.szj.demo.dtos.order.OrderDTO;
import com.szj.demo.model.*;
import com.szj.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
   public OrderDTO createOrder(User user, Address address) {
        try {
                Cart cart = cartRepository.findCartByUser(user)
                        .orElseThrow(() -> new RuntimeException("Cart not found"));



            Order order = new Order();
            order.setUser(user);
            order.setStatus("PENDING");
            order.setOrderDate(LocalDate.now());
            if(address == null){
                address = user.getAddress();
            }
            order.setDeliveryAddress(address);

                double totalAmount = 0.0;
                List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getCartProduct());
                    orderItem.setQuantity(cartItem.getCartItemQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                }).toList();

                for (OrderItem orderItem : orderItems) {
                    totalAmount += orderItem.getProduct().getPrice() * orderItem.getQuantity();
                }

                order.setOrderItems(orderItems);
                order.setTotalPrice(totalAmount);

                for(OrderItem orderItem : orderItems){
                    Product product = orderItem.getProduct();
                    int newStock = product.getStock() - orderItem.getQuantity();
                    if (newStock < 0) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
                    }
                }

                Order savedOrder = orderRepository.save(order);
                orderItemRepository.saveAll(orderItems);

                cart.getCartItems().clear();
                cartRepository.save(cart);

            return new OrderDTO(savedOrder);
        } catch (RuntimeException e) {
            throw new RuntimeException("Order creation failed: " + e.getMessage());
        } catch (Exception e){
            throw new IllegalArgumentException("Something went wrong: " +e.getMessage());
        }
    }

    @Transactional
    public void processPayment(User user, Long orderId){
        Optional<Order> optOrder = orderRepository.findOrdersByUserId(user.getId());
        if(optOrder.isEmpty()){
            throw new IllegalArgumentException("Order does not exits in repository");
        }
        Order order = optOrder.get();

        double totalAmount = order.getTotalPrice();

            if (user.getBalance() < totalAmount) {
                throw new RuntimeException("Insufficient balance! ");
            }

            user.setBalance(user.getBalance() - totalAmount);
            userRepository.save(user);
            updateProductStock(order);
    }

    private void updateProductStock(Order order){
        for(OrderItem orderItem : order.getOrderItems()){
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() - orderItem.getQuantity());
            if(product.getStock() < 0){
                throw new RuntimeException("Product stock must be at least 1");
            }
            productRepository.save(product);
        }
    }
}
