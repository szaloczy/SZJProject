package com.szj.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szj.demo.enums.AuthenticationLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Document
@Entity
@Data
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @JsonProperty("username")
    private String username;

    @Column
    @JsonProperty("password")
    private String password;

    @JsonProperty("accessLevel")
    private AuthenticationLevel accessLevel;

    @Column
    @JsonProperty("balance")
    private Double balance = 0.0;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @JsonProperty("address")
    private Address address;

    @Column
    private String email;

    @JsonProperty("profilePicture")
    private UUID profilePicture;

    public User() {
    }

    public User(String username, String password){
        this(username, password, AuthenticationLevel.PRIVATE);
    }

    public User(String username, String password, AuthenticationLevel accessLevel){
        if (!Pattern.matches("^[a-zA-Z0-9]+$", username)) throw new IllegalStateException("Username holds illegal character(s)!"); //kis/nagy betű illetve számjegyek, legalább 1
        if (username.length() < 3) throw new IllegalStateException("Username must be at least 3 character long!");
        if (password.length() < 3) throw new IllegalStateException("Password must be at least 3 character long!");

        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
    }

    public int hashCode() {
        return username.hashCode() + password.hashCode() + accessLevel.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    public void setProfilePicture(UUID profilePictureId) {
    }
}
