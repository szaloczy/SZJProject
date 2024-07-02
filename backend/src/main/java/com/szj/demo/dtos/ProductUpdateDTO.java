package com.szj.demo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ProductUpdateDTO {

    @JsonProperty
    private Long productId;
    @JsonProperty
    private String productName;
    @JsonProperty
    private String productDescription;
    @JsonProperty
    private Double price;

    public ProductUpdateDTO(){};

    public ProductUpdateDTO(Long productId, String name, String description, Double price) {
        this.productId = productId;

        if(name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty!");
        this.productName = name;

        if (description.isEmpty()) throw new IllegalArgumentException("Description cannot be empty");
        this.productDescription = description;

        if (price < 0 ) throw new IllegalArgumentException("Price must be grater than zero!");
        this.price = price;
    }
}
