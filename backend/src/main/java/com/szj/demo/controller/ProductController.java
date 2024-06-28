package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.ProductDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Product;
import com.szj.demo.model.ProductRequest;
import com.szj.demo.service.ProductService;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping()
    public List<Product> getAll(){
        return productService.getAll();
    }
/*
    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody Product myProduct){
        try{
            ProductDTO productDTO = productService.createProduct(userService.currentUser(), myProduct);
            return ResponseEntity.ok().body(new ApiResponse<>(true, productDTO,""));
        } catch (IllegalStateException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Product creation failed!"));
        } catch (HttpServerErrorException.InternalServerError) {
            return ResponseEntity<>
        }
    }
˛*/
}
