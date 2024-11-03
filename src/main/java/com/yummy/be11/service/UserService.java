package com.yummy.be11.service;

import com.yummy.be11.model.User;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    User findByUsername(String username);
    List<User> findAllUsers();
    User updateUser(String currentUsername, User updatedUser);
    void deleteUserById(Long id);
}
