package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.product.CartDTO;
import com.szj.demo.dtos.product.CartItemDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.*;
import com.szj.demo.service.CartService;
import com.szj.demo.service.ProductService;
import com.szj.demo.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;


    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(Long userId) {
        try {
            Cart cart = cartService.getCartByUser(userId);
            CartDTO cartDTO = new CartDTO(cart);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, cartDTO, ""));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Something went wrong"));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PUBLIC)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItemDTO>> addItemToCart(@RequestBody CartItemDTO cartItemDTO) {
        try {
            Optional<Product> optProduct = productService.findProductByProductId(cartItemDTO.getProductId());
            if (optProduct.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product does not exits in repository!"));
            }
            Product product = optProduct.get();
            CartItemDTO cart = cartService.addItemToCart(userService.currentUser(), product, cartItemDTO.getQuantity());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, cart, ""));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Something went wrong!"));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping("/remove")
    public ResponseEntity<ApiResponse<Cart>> removeItemFromCart(@RequestBody CartItemDTO cartItemDTO){
        try {
            Optional<Product> optProduct = productService.findProductByProductId(cartItemDTO.getProductId());
            if (optProduct.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Product does not exits!"));
            }

            Product product = optProduct.get();
            Cart cart = cartService.removeItemFromCart(userService.currentUser(), product);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, cart, ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Something went wrong"));
        }
        }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        try {
            cartService.clearCart(userService.currentUser());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, ""));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,null,"Token expired!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}
