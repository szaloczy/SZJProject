package com.szj.demo.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class updateOrderDTO {
    private String id;
    private Integer quantity;
}
