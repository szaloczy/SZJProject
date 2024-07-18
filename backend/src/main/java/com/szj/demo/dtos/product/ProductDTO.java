package com.szj.demo.dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.model.Product;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ProductDTO {
    @JsonProperty
    private Long productId;

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
    private String condition;

    @JsonProperty
    private Boolean available;

    public ProductDTO(Product product){
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.seller = product.getSeller();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.creationDate = product.getCreationDate();
        this.condition = product.getProductCondition();
        this.available = product.getStock() > 0;
    }
}
