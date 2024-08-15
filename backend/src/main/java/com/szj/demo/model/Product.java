package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.dtos.product.ProductUpdateDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private String productCondition;


    public Product() {}

    public Product(String sellerName, String productName, String description, String condition, Double price, Integer stock) {
        this.seller = sellerName;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.creationDate = LocalDateTime.now();
        this.productCondition = condition;
        this.stock = stock;
    }

    public void update(ProductUpdateDTO modification) {
        if(productId != modification.getProductId())
            throw new IllegalArgumentException("Cannot update because the product id does not match");

        if(modification.getProductName().isEmpty())
           throw new IllegalArgumentException("Product name can't be empty");
        if(modification.getProductName().length() < 3 || modification.getProductName().length() > 25)
            throw new IllegalArgumentException("Product name cannot be less than 3, or more than 50 characters!");

        this.productName = modification.getProductName();

        if(modification.getProductDescription().isEmpty())
            throw new IllegalArgumentException("Product description cannot be empty");
        if(modification.getProductDescription().length() < 3 || modification.getProductDescription().length() > 100)
            throw new IllegalArgumentException("Product description cannot be less than 3, or more than 100 character");

        this.description = modification.getProductDescription();

        if(modification.getPrice() < 0)
            throw new IllegalArgumentException("Product price must be grater than 0!");

        this.price = modification.getPrice();
    }
}
