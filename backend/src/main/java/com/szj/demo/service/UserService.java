package com.szj.demo.service;

import com.szj.demo.exception.UserNotFoundException;
import com.szj.demo.model.AuthenticationResponse;
import com.szj.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAllUser();

    User createUser(User user);

    void deleteUser(Long id) throws Exception;

    AuthenticationResponse login(User user);
}
