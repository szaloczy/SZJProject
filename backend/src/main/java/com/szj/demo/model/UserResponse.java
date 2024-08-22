package com.szj.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private String username;
    private double balance;
    private String email;
    private Address address;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.balance = user.getBalance();
        this.email = user.getEmail();
        this.address = user.getAddress();
    }
}
