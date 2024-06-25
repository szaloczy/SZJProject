package com.szj.demo.model;

import lombok.Data;

@Data
public class UserResponse {
    private String username;
    private double balance;

    public UserResponse(User user){
        this.username = user.getUsername();
    }
}
