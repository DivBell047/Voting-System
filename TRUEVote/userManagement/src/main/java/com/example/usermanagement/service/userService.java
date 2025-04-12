package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.repo.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class userService {
    @Autowired
    private userRepository userRepository;

    public User createUser(String username, String email, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setRole(role);
        user.setCreatedAt(Instant.now().toString());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        // findById is provided by JpaRepository
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean userExistsById(Long id) {
        if (id == null) {
            return false;
        }
        // existsById is provided by JpaRepository
        return userRepository.existsById(id);
    }
}
