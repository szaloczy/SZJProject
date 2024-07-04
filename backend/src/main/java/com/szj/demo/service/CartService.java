package com.szj.demo.service;

import com.szj.demo.dtos.cart.AddToCartDTO;
import com.szj.demo.dtos.cart.CartItemDTO;
import com.szj.demo.model.Cart;
import com.szj.demo.model.Product;
import com.szj.demo.model.User;
import com.szj.demo.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void addToCart(AddToCartDTO addToCartDTO, Product product, User user) {
        Cart cart = new Cart(product, addToCartDTO.getQuantity(), user);
        cartRepository.save(cart);
    }


}
