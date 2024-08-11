package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.Address;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.UpdateBalanceRequest;
import com.szj.demo.model.User;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PUBLIC)
    @PostMapping(value = "auth/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        try {
            User savedUser = userService.register(user.getUsername(), user.getPassword());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, savedUser, ""));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PUBLIC)
    @PostMapping(value = "auth/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody User user) {
        try {
            String token = userService.login(user);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, token, ""));
        }catch (NoSuchElementException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping(value = "auth/logout")
    public ResponseEntity<HttpStatus> logout() {
        try {
            userService.logout();
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping(value = "address")
    public ResponseEntity<ApiResponse<Address>> createAddress(@RequestBody Address address) {
        try{
            userService.createAddress(userService.currentUser(),address);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, address, ""));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping(value = "balance")
    public ResponseEntity<ApiResponse<String>> updateBalance(@RequestBody UpdateBalanceRequest updateBalanceRequest) {
        try {
            User currentUser = userService.currentUser();
            userService.updateUserBalance(currentUser, updateBalanceRequest);
            return ResponseEntity.ok().body(new ApiResponse<>(true, "Your balance has been updated successfully", ""));
        }catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping(value = "address")
    public ResponseEntity<ApiResponse<List<Address>>> getAddresses() {
        try {
            List<Address> addresses =  userService.getAddresses(userService.currentUser());
            return ResponseEntity.ok(new ApiResponse<>(true, addresses, ""));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping(value = "balance")
    public ResponseEntity<ApiResponse<Double>> getBalance(@RequestParam("id") Long userId){
        try {
            double balance = userService.getBalance(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, balance, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}
