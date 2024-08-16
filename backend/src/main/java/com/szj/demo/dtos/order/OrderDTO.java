package com.szj.demo.dtos.order;

import com.szj.demo.model.Address;
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
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDate orderDate;
    private String orderStatus;
    private double totalPrice;
    private Address address;

    public OrderDTO(Order order) {
        this.orderItems = order.getOrderItems();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.address = order.getAddress();
    }
}
