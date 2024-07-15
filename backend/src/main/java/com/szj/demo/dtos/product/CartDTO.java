package com.szj.demo.dtos.product;

import com.szj.demo.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long cartId;
    private List<CartItemDTO> cartItems;

    public CartDTO(Cart cart) {
        this.cartId = cart.getCartId();
        this.cartItems = cart.getCartItems().stream()
                .map(CartItemDTO::new)
                .collect(Collectors.toList());
    }
}
