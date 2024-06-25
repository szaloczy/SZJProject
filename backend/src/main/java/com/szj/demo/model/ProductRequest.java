package com.szj.demo.model;

import com.szj.demo.enums.ProductCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    private String productDescription;
    private String productName;
    private ProductCondition productCondition;
    private Double price;

    public ProductRequest(String productDescription, String productName, ProductCondition productCondition, Double price) {
        this.productDescription = productDescription;
        this.productName = productName;
        this.productCondition = productCondition;
        this.price = price;
    }
}
