package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.order.OrderDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Order;
import com.szj.demo.service.OrderService;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder() {
        try {
            OrderDTO orderDTO = orderService.createOrder(userService.currentUser());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, orderDTO, ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping()
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@RequestParam("id") Long userId) {
        try {
            OrderDTO orderDTO = orderService.getOrder(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, orderDTO, ""));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Something went wrong"));
        }
    }
}
