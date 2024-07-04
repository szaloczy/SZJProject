package com.szj.demo.repository;

import com.szj.demo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAllByUserIdOrderByCreationDateDesc(Long userId);
}
