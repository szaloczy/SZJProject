package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.cart.AddToCartDTO;
import com.szj.demo.dtos.cart.CartDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Product;
import com.szj.demo.service.CartService;
import com.szj.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ProductService productService;

    /*
    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@RequestBody AddToCartDTO addToCartDTO) {
        Product product = productService.get
    }*/

}
