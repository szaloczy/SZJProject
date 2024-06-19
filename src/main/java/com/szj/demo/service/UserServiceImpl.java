package com.szj.demo.service;

import com.szj.demo.exception.UserNotFoundException;
import com.szj.demo.model.User;
import com.szj.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not exists"));

        userRepository.delete(user);
    }

    @Override
    public void updateUser(User user, Long id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()){
            User updatedUser = optionalUser.get();

            updatedUser.setId(user.getId());
            updatedUser.setName(user.getName());
            updatedUser.setPassword(user.getPassword());

            userRepository.save(updatedUser);
        } else {
            throw new UserNotFoundException("User with " + id + "not found");
        }

    }


}
