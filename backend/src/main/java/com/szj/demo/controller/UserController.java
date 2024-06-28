package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.User;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping( value = "auth/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody User user) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, userService.login(user), ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(null);
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
}
