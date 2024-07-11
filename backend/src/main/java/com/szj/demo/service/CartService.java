package com.szj.demo.service;

import com.szj.demo.dtos.product.CartDTO;
import com.szj.demo.model.Cart;
import com.szj.demo.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;


}
