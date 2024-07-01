package com.szj.demo.dtos;

import lombok.Getter;

@Getter
public class ProductUpdateDTO {

    private Long productId;
    private String productName;
    private String productDescription;
    private Double price;

}
