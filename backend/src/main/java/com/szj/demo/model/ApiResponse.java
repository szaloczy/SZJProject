package com.szj.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String error;

    public ApiResponse(T data){
        this.success = true;
        this.data = data;
        this.error = "";
    }

    public ApiResponse(String error){
        this.success = false;
        this.data=null;
        this.error = error;
    }

    public ApiResponse(T data, String error){
        this.success = false;
        this.data = data;
        this.error = error;
    }

    public ApiResponse(boolean success, T data, String error){
        this.success = success;
        this.data = data;
        this.error = error;
    }
}
