package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.enums.ProductCondition;
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
    @NotNull
    @Basic(optional = false)
    private String productName;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    Category category;

    @JsonProperty
    private ProductCondition productCondition;

    @JsonProperty
    private Double price;
}
