package com.szj.demo.service;

import com.szj.demo.model.*;
import com.szj.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public Order createOrder(User user) {
       Cart cart = getCartForUser(user);
       checkPendingOrder(user);

       Order order = buildOrderFromCart(cart);
       clearCart(cart);

       return orderRepository.save(order);
   }

    private Cart getCartForUser(User user) {
       return cartRepository.findCartByUserId(user.getId()).orElseThrow(() -> new IllegalStateException("Your cart is empty"));
    }

    private void checkPendingOrder(User user) {
       Optional<Order> existingOrder = orderRepository.findOrdersByUserIdAndStatus(user.getId(), "PENDING");
       if(existingOrder.isPresent()){
           throw new IllegalStateException("You already have a pending order");
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
    public void processPayment(User user, Address address) {
       Order order = getOrderForUser(user);
       validateOrderForPayment(order, user);
       performPayment(user, order);
       updateAddressIfNeeded(order, user, address);
       updateProductStock(order);
    }

    private Order getOrderForUser(User user) {
       return orderRepository.findOrdersByUserIdAndStatus(user.getId(), "PENDING")
               .orElseThrow(() -> new IllegalStateException("Order does not exits"));
    }

    private void validateOrderForPayment(Order order, User user) {
       if(order.getStatus().equals("PAID")){
           throw new IllegalStateException("Your order is already paid");
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

    private void updateAddressIfNeeded(Order order, User user, Address address) {

        Optional<User> optUser = userRepository.findUserByUsername(user.getUsername());
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("User does not exist in repository");
        }

        User updatedUser = optUser.get();

       Optional<Address> existingAddress = addressRepository.findByDetails(address.getCountry(),
               address.getCity(),
               address.getStreet(),
               address.getZipCode());

       Address addressToUse;

        if (existingAddress.isPresent()) {
            addressToUse = existingAddress.get();
        } else {
            addressToUse = addressRepository.save(address);
        }
        if (!updatedUser.getAddresses().contains(addressToUse)) {
            updatedUser.getAddresses().add(addressToUse);
        }

       order.setDeliveryAddress(addressToUse);
        userRepository.save(updatedUser);

        addressToUse.setUser(updatedUser);
        addressRepository.save(addressToUse);

    }

    private void performPayment(User user, Order order) {
        double totalAmount = order.getTotalPrice();
        user.setBalance(user.getBalance() - totalAmount);
        userRepository.save(user);
        order.setStatus("PAID");
        orderRepository.save(order);
    }

}
