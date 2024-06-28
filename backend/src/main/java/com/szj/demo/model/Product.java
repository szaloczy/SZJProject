package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.enums.ProductCondition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue
    private long productId;

    @JsonProperty
    @Column(nullable = false)
    private String productName;

    @JsonProperty
    private String seller;

    @JsonProperty
    private String description;

    @JsonProperty
    @Column(nullable = false)
    private Double price;

    @JsonProperty
    private Integer stock;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    private ProductCondition productCondition;

    @JsonProperty
    private Boolean available;

    public Product() {}

    public Product(String sellerName, String productName, String description, ProductCondition condition, Double price, Integer stock) {
        this.seller = sellerName;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.creationDate = LocalDateTime.now();
        this.productCondition = condition;
        this.stock = stock;
        this.available = stock > 0;
    }
}
