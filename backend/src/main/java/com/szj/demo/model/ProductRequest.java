package com.szj.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    private String productName;
    private String productDescription;
    private String productCondition;
    private Double price;
    private Integer stock;

    public ProductRequest(String productName, String productDescription, String productCondition, Double price, Integer stock) {
        this.productDescription = productDescription;
        this.productName = productName;
        this.productCondition = productCondition;
        this.price = price;
        this.stock = stock;
    }
}
