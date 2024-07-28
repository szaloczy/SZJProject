package com.szj.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateBalanceRequest {
    private String cardNumber;
    private String holderName;
    private String expirationDate;
    private String cvv;
    private Double newBalance;

}
