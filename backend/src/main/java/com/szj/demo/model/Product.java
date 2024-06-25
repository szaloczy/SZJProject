package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.enums.ProductCondition;
import com.szj.demo.enums.ProductState;
import jakarta.persistence.Basic;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Data
@Getter
@Setter
public class Product {
    @Id
    @JsonProperty
    private UUID productId;

    @JsonProperty
    private String seller;

    @JsonProperty
    @Basic(optional = false)
    private String productName;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    private String description;

    @JsonProperty
    private ProductCondition productCondition;

    @JsonProperty
    private Double price;

    @JsonProperty
    private ProductState productState;

    @JsonProperty
    private boolean available;

    public Product(String seller, String description, String productName, LocalDateTime creationDate, Double price){
        productId = UUID.randomUUID();
        this.seller = seller;
        this.description = description;
        this.productName = productName;
        this.creationDate = creationDate;
        this.price = price;
        this.available = true;
        this.productState = ProductState.OPEN;
    }
}
