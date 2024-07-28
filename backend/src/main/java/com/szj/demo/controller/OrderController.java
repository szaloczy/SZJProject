package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.order.OrderDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.Address;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Order;
import com.szj.demo.model.User;
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
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@RequestBody Address address) {
        try {
            OrderDTO orderDTO = orderService.createOrder(userService.currentUser(), address);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, orderDTO, ""));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "Your token is expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "An error occurred while creating the order"));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<String>> processPayment(@RequestParam("orderId") Long orderId) {
        try {
            orderService.processPayment(userService.currentUser(), orderId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Payment processed successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}
