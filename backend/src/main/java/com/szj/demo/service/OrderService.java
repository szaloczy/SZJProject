package com.szj.demo.service;

import com.szj.demo.dtos.order.OrderDTO;
import com.szj.demo.exception.InvalidCartException;
import com.szj.demo.exception.InvalidOrderException;
import com.szj.demo.model.*;
import com.szj.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public void createOrder(User user) {
        try {
            Cart cart = getCartForUser(user);
            checkPendingOrder(user);
            validateCart(cart);

            Order order = buildOrderFromCart(cart);

            orderRepository.save(order);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(e.getMessage());
        } catch (InvalidCartException | InvalidOrderException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateCart(Cart cart) throws InvalidCartException {
        if(isCartEmpty(cart)){
            throw new InvalidCartException("Your cart is empty");
        }
    }

    private boolean isCartEmpty(Cart cart) {
        return cart.getCartItems().isEmpty();
    }

    private Cart getCartForUser(User user) {
        return cartRepository.findCartByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("Your cart does not exists!"));
    }

    private void checkPendingOrder(User user) throws InvalidOrderException {
        Optional<Order> existingOrder = orderRepository.findOrdersByUserIdAndStatus(user.getId(), "PENDING");
        if(existingOrder.isPresent()){
            throw new InvalidOrderException("You already have a PENDING order! Buy or cancelled it");
        }
    }

    private Order buildOrderFromCart(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");

        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getCartProduct());
            orderItem.setQuantity(cartItem.getCartItemQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).toList();

        order.setOrderItems(orderItems);
        order.setTotalPrice(cart.getCartItems()
                .stream()
                .mapToDouble(cartItem -> cartItem.getCartProduct().getPrice() * cartItem.getCartItemQuantity()).sum());

        return order;
    }

    private void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cartRepository.save(cart);
        cartItemRepository.deleteCartItemsByCart_CartId(cart.getCartId());
    }

    @Transactional
    public void processPayment(User user, Address address) throws InvalidOrderException {
        try {
            Order order = getOrderForUser(user);
            validateOrderForPayment(order, user);
            Address savedAddress = checkAddress(address);
            order.setAddress(savedAddress);
            performPayment(user, order);
            Cart cart = getCartForUser(user);
            clearCart(cart);
            updateProductStock(order);
        } catch (IllegalStateException | InvalidOrderException e) {
            throw new InvalidOrderException(e.getMessage());
        }

    }

    private Address checkAddress(Address address) {
        Optional<Address> existingAddress = addressRepository.findByDetails(address.getCountry(), address.getStreet(), address.getCity(), address.getZipCode());

        if(existingAddress.isPresent()) {
            return existingAddress.get();
        } else {
             Address newAddress = new Address(address.getCountry(), address.getCity(),address.getStreet(),address.getZipCode());
             return addressRepository.save(newAddress);
        }
    }

    private Order getOrderForUser(User user) throws InvalidOrderException {
        return orderRepository.findOrdersByUserIdAndStatus(user.getId(), "PENDING")
                .orElseThrow(() -> new InvalidOrderException("Order not found"));
    }

    private void validateOrderForPayment(Order order, User user) {
        if(!order.getStatus().equals("PENDING")){
            throw new IllegalStateException("Invalid order status");
        }

        double totalAmount = order.getTotalPrice();
        if(user.getBalance() < totalAmount){
            throw new IllegalStateException("Insufficient balance");
        }
    }

    private void updateProductStock(Order order) {
        for(OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() - orderItem.getQuantity());
            if(product.getStock() < 0) {
                throw new IllegalStateException("Product stock must be at least 1");
            }
            productRepository.save(product);
        }
    }

    private void performPayment(User user, Order order) {
        double totalAmount = order.getTotalPrice();
        user.setBalance(user.getBalance() - totalAmount);
        userRepository.save(user);
        order.setStatus("PAID");
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId){
        Optional<Order> optOrder = orderRepository.findOrderByOrderId(orderId);
        if(optOrder.isEmpty()){
            throw new NoSuchElementException("Order does not exits");
        }

        orderRepository.deleteByOrderId(orderId);
    }

}
