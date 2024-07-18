package com.szj.demo.dtos.cart;

import com.szj.demo.model.Cart;
import com.szj.demo.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartItemDTO {
    private Long productId;
    private Integer quantity;

    public CartItemDTO(Cart cart){
        if (cart != null && !cart.getCartItems().isEmpty()) {
            this.productId = cart.getCartItems().get(0).getCartItemId();
            this.quantity = cart.getCartItems().get(0).getCartItemQuantity();
        }
    }

    public CartItemDTO(CartItem cartItem) {
        productId = cartItem.getCartProduct().getProductId();
        quantity = cartItem.getCartItemQuantity();
    }
}
