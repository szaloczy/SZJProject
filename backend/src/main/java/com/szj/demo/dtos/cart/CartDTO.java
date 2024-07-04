package com.szj.demo.dtos.cart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartDTO {
    private List<CartItemDTO> cartItems;
    private double totalCost;

    public CartDTO(){}

    public CartDTO(List<CartItemDTO> cartItemDTOList, double totalCost){
        this.cartItems = cartItemDTOList;
        this.totalCost = totalCost;
    }
}
