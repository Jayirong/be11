package com.yummy.be11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yummy.be11.model.User;
import com.yummy.be11.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/me")
    public User getUserDetails(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @PutMapping("/update")
    public User updateUser(@RequestBody User user, Principal principal) {
        return userService.updateUser(principal.getName(), user);
    }
}
