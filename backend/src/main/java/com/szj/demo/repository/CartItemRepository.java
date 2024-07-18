package com.szj.demo.repository;

import com.szj.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteCartItemsByCart_CartId (Long cartId);
}
