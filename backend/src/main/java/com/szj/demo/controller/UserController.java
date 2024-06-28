package com.szj.demo.controller;

import com.szj.demo.model.AuthenticationResponse;
import com.szj.demo.model.User;
import com.szj.demo.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    public static final String USER_API_PATH = "api/user";
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping()
    public List<User> findAllUser(){
       return  userService.findAllUser();
    }

    @PostMapping(value = "auth/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.createUser(user);
            URI location = fromPath(USER_API_PATH)
                    .path("/{username}")
                    .buildAndExpand(user.getUsername())
                    .toUri();
            return ResponseEntity.created(location).body(savedUser);
        } catch (IllegalStateException illegalStateException) {
            return ResponseEntity.badRequest().body(user);
        }
    }

    @PostMapping(value = "auth/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User user){
        return ResponseEntity.ok(userService.login(user));
    }

    @GetMapping(value = "user/detail")
    public ResponseEntity<UserResponse> getDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        User user = jwtService.extractUser(jwtToken);
        return  ResponseEntity.ok(new UserResponse(user));

    }
}
