package com.szj.demo.controller;

import com.szj.demo.dtos.ProductDTO;
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

    @PostMapping()
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken, @RequestBody ProductRequest myProduct){
        try {
            ProductDTO productDTO = productService.createProduct(jwtToken, myProduct);
            return ResponseEntity.ok().body(new ApiResponse<>(productDTO));
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("Product creation failed"));
        } catch (HttpServerErrorException.InternalServerError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Endpoint not found"));
        }
    }

}
