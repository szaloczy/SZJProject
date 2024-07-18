package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Order;
import com.szj.demo.model.OrderItem;
import com.szj.demo.service.OrderService;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<Order>> createOrder() {
        try {
            Order order = orderService.createOrder(userService.currentUser());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, order, ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}
