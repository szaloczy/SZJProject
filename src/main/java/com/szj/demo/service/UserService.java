package com.szj.demo.service;

import com.szj.demo.exception.UserNotFoundException;
import com.szj.demo.model.User;

import java.util.List;

public interface UserService {

    List<User> findAllUser();

    User createUser(User user);

    void deleteUser(Long id) throws Exception;

    void updateUser(User user, Long id) throws UserNotFoundException;
}
