package com.szj.demo.dtos.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddToCartDTO {
    private Integer id;
    private @NotNull Long productId;
    private @NotNull int quantity;

    public AddToCartDTO() {}
}
