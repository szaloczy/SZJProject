package com.szj.demo.controller;

import com.szj.demo.exception.UserNotFoundException;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.User;
import com.szj.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping()
    public List<User> findAllUser(){
       return  userService.findAllUser();
    }

    @PostMapping()
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(@PathVariable Long id) throws Exception {
        userService.deleteUser(id);
        ApiResponse response = new ApiResponse();
        response.setMessage("User deleted successfully!");
        response.setStatus(true);
        return response;
    }
}
