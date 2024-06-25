package com.szj.demo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.enums.ProductCondition;
import com.szj.demo.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDTO {

    @JsonProperty
    private String productId;

    @JsonProperty
    private String seller;

    @JsonProperty
    private String productName;

    @JsonProperty
    private ProductCondition productCondition;

    @JsonProperty
    private String productDescription;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    private Double price;

    @JsonProperty
    private boolean available;

    @JsonProperty
    private String buyerId;

    public ProductDTO(Product product){
        productId = product.getProductId().toString();
        seller = product.getSeller();
        productName = product.getProductName();
        productCondition = product.getProductCondition();
        productDescription = product.getDescription();
        creationDate = product.getCreationDate();
        price = product.getPrice();
        available = product.isAvailable();
        buyerId = null;
    }
}
