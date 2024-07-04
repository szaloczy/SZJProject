package com.szj.demo.dtos.cart;

import com.szj.demo.model.Cart;
import com.szj.demo.model.Product;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private Long id;
    private @NotNull int quantity;
    private @NotNull Product product;

    public CartItemDTO(){}

    public CartItemDTO(Cart cart){
        this.setId(cart.getId());
        this.setQuantity(cart.getQuantity());
        this.setProduct(cart.getProduct());
    }
}
