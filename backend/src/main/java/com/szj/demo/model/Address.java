package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @OneToOne
   @JoinColumn(name = "user_id")
   @JsonIgnore
   private User user;

   private String country;
   private String city;
   private String street;
   private String zipCode;
}
