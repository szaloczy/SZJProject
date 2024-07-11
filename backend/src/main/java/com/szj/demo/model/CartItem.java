package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cartItemId;


    @OneToOne
    @JsonIgnoreProperties(value={
            "productId",
            "seller",
            "quantity"
    })

    private Product cartProduct;

    private Integer cartItemQuantity;
}
