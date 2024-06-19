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
public class UserController {
    @Autowired
    private UserService userService;

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

    @PutMapping("/{id}")
    public void updateUser(@RequestBody User user, @PathVariable Long id){
        try {
            userService.updateUser(user,id);
        } catch (UserNotFoundException e) {
            throw new RuntimeException("User not found");
        }
    }
}
