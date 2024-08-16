package com.szj.demo.repository;

import com.szj.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    Optional<Order> findOrdersByUserIdAndStatus(Long id,String status);
    Optional<Order> findOrderByOrderId(Long orderId);
    List<Order> findOrdersByUserId(Long userId);
    void deleteByOrderId(Long orderId);
    List<Order> findByAddressId(Long deliveryAddressId);
}
