package com.szj.demo.repository;

import com.szj.demo.model.Cart;
import com.szj.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findCartByCartId(Long id);
    Optional<Cart> findCartByUser(User user);
    Optional<Cart> findCartByUserId(Long userId);
}
