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
   public Order createOrder(User user) {
      Optional<Cart> optCart = cartRepository.findCartByUserId(user.getId());

      if(optCart.isEmpty()){
          throw new RuntimeException("Your cart is empty!");
      }

      Cart cart = optCart.get();

      Order order = new Order();
      order.setUser(cart.getUser());
      order.setOrderDate(LocalDate.now());
      order.setStatus("Pending");
      //order.setDeliveryAddress(user.getAddress().getCountry().concat("-").concat(user.getAddress().getCity()));

      List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
          OrderItem orderItem = new OrderItem();
          orderItem.setProduct(cartItem.getCartProduct());
          orderItem.setQuantity(cartItem.getCartItemQuantity());
          orderItem.setOrder(order);
          return orderItem;
      }).toList();

      order.setOrderItems(orderItems);
      order.setTotalPrice(cart.getCartItems().stream().mapToDouble(cartItem -> cartItem.getCartProduct().getPrice() * cartItem.getCartItemQuantity()).sum());

      cart.getCartItems().clear();
      cartRepository.save(cart);

      return orderRepository.save(order);
    }

    @Transactional
    public void processPayment(User user, Address deliveryAddress){
            Optional<Order> optOrder = orderRepository.findOrdersByUserId(user.getId());
            if(optOrder.isEmpty()){
                throw new IllegalArgumentException("Order does not exits in repository");
            }
            Order order = optOrder.get();
            if(order.getStatus().equals("PAID")){
                throw new RuntimeException("Your order already paid!");
            }
            double totalAmount = order.getTotalPrice();
            if (user.getBalance() < totalAmount) {
                throw new RuntimeException("Insufficient balance! ");
            }

            user.setBalance(user.getBalance() - totalAmount);
            userRepository.save(user);
            order.setStatus("PAID");
            order.setDeliveryAddress(deliveryAddress);
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
