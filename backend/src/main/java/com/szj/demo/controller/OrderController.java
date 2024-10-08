package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.order.OrderDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.Address;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Order;
import com.szj.demo.repository.OrderRepository;
import com.szj.demo.service.OrderService;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<String>> createOrder() {
        try {
            orderService.createOrder(userService.currentUser());
            return ResponseEntity.ok(new ApiResponse<>(true,"Your order created successfully",""));
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,null,e.getMessage()));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "Your token is expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "An error occurred while creating the order"));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<String>> processPayment(@RequestBody Address deliveryAddress) {
        try {
            orderService.processPayment(userService.currentUser(), deliveryAddress);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Payment processed successfully", ""));
        } catch (InvalidTokenException e){
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @DeleteMapping()
    public ResponseEntity<ApiResponse<String>> deleteOrder() {
        try {
            orderService.deleteOrder(userService.currentUser());
            return ResponseEntity.ok(new ApiResponse<>(true, "Order deleted successfully", ""));
        } catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Order not found"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

}
