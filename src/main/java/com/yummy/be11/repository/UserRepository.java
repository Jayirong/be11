package com.yummy.be11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yummy.be11.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
