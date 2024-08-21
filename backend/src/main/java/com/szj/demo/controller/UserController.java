package com.szj.demo.controller;

import com.szj.common.fileServer.model.FileEntity;
import com.szj.common.fileServer.services.FileServerService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileServerService fileServerService;

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping(value="file-server")
    public ResponseEntity<ApiResponse<FileEntity>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            FileEntity fileEntity = fileServerService.upload(file);
            userService.changeProfilePicture(userService.currentUser(), fileEntity.getId());
            fileServerService.setFileEntityInUse(fileEntity.getId(), true);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, fileEntity, ""));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.toString()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping("/email")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody String email) {
        try{
            User user = userService.saveUserEmail(userService.currentUser(), email);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "You saved your email successfully: " + user.getEmail(),""));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PutMapping("/email")
    ResponseEntity<ApiResponse<String>> updateUserEmail(@RequestBody String email){
        try {
         User user = userService.updateUserEmail(userService.currentUser(),email);
         return ResponseEntity.ok().body(new ApiResponse<>(true, "Your email updated successfully: " + user.getEmail(), ""));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "Invalid token"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping("/email")
    ResponseEntity<ApiResponse<String>> getUserEmail(){
        try{
        User user = userService.getUserEmail(userService.currentUser());
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Your email found: " + user.getEmail(), ""));
        } catch(InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "Invalid token"));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

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
    public ResponseEntity<ApiResponse<String>> createAddress(@RequestBody Address address) {
        try{
            userService.saveAddress(userService.currentUser(),address);
            return ResponseEntity.ok().body(new ApiResponse<>(true, "you address has been saved.", ""));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping(value = "address")
    public ResponseEntity<ApiResponse<Address>> getUserAddress() {
        try {
            Address address =  userService.getAddressByUserId(userService.currentUser());
            return ResponseEntity.ok(new ApiResponse<>(true, address, ""));
        } catch (Exception e){
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
    @GetMapping(value = "balance")
    public ResponseEntity<ApiResponse<Double>> getBalance(){
        try {
            double balance = userService.getBalance(userService.currentUser());
            return ResponseEntity.ok(new ApiResponse<>(true, balance, ""));
        } catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
}
