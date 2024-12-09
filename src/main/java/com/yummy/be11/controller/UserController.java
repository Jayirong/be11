package com.yummy.be11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.yummy.be11.model.User;
import com.yummy.be11.service.UserService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.registerUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User is not authenticated"));
        }
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User user, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User is not authenticated"));
        }
        try {
            User updatedUser = userService.updateUser(principal.getName(), user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
