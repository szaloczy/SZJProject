package com.szj.demo.controller;

import com.szj.common.fileServer.model.FileEntity;
import com.szj.common.fileServer.services.FileServerService;
import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.*;
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
    @GetMapping(value="/detail")
    public ResponseEntity<UserResponse> getDetail() {
        try {
            return ResponseEntity.ok(new UserResponse(userService.currentUser()));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PutMapping(value = "update")
    public ResponseEntity<UserResponse> updateUserDetails(@RequestBody User user) {
        try {
            userService.updateUserDetails(userService.currentUser(), user);
                return ResponseEntity.ok(new UserResponse(user));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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
}
