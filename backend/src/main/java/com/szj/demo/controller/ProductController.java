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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping()
    public List<Product> getAll(){
        return productService.getAll();
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductRequest myProduct){

        Logger logger = LoggerFactory.getLogger(getClass());
        try{
            ProductDTO productDTO = productService.createProduct(userService.currentUser(), myProduct);
            return ResponseEntity.ok().body(new ApiResponse<>(true, productDTO,""));
        } catch (InvalidTokenException e) {
            logger.error("Invalid token: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "Invalid token!"));
        }
        catch (IllegalStateException e) {
            logger.error("Product creation failed: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Product creation failed!"));
        } catch (HttpServerErrorException.InternalServerError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Endpoint not found"));
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

}
