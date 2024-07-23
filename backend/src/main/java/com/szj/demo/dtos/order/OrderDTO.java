package com.szj.demo.dtos.order;

import com.szj.demo.model.Order;
import com.szj.demo.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDate orderDate;
    private String orderStatus;
    private double totalPrice;

    public OrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUser().getId();
        this.orderItems = order.getOrderItems();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.totalPrice = order.getTotalPrice();

    }
}
