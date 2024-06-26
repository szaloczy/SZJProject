package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Data
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    @JsonProperty
    private UUID id;

    @JsonProperty
    @Column(nullable = false, unique = true)
    private String name;

    // Default constructor for JPA
    public Category() {}

    public Category(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }
}
