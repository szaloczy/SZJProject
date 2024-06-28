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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "product_id", updatable = false, nullable = false, length = 36)
    @JsonProperty
    private UUID productId;

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

    public Product(String name, String description, Double price, String brand, String category, Integer stock, String imageUrl, ProductCondition condition) {
        this.productId = UUID.randomUUID();
        this.productName = name;
        this.description = description;
        this.price = price;
        this.creationDate = LocalDateTime.now();
        this.productCondition = condition;
        this.available = stock > 0;
    }
}
