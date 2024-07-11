package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "cart")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cartId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    private Double cartTotal;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private User user;

}
