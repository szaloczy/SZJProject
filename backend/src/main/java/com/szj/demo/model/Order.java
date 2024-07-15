package com.szj.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/*
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data*/
public class Order {
    /*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    //@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    private User user;

    private LocalDateTime orderDate;
    private String status;

    @Transient
    public Double getTotalAmount() {
        return orderItems.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();
    }
*/
}
