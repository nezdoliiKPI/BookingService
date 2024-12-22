package com.example.lab2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab2.entity.User;
import com.example.lab2.entity.User.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPassword(String password);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Iterable<User> findAllByUserStatus(UserStatus status);
}
