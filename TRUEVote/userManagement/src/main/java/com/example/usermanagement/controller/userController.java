package com.example.usermanagement.controller;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.userService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Endpoints for user management")
public class userController {

    @Autowired
    private userService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = User.class)))
    public User registerUser(
            @Parameter(description = "Username of the new user", required = true) @RequestParam String username,
            @Parameter(description = "Email of the new user", required = true) @RequestParam String email,
            @Parameter(description = "Password of the new user", required = true) @RequestParam String password,
            @Parameter(description = "Role of the new user", required = true) @RequestParam String role) {
        return userService.createUser(username, email, password, role);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves a user by their username.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public Optional<User> getUser(
            @Parameter(description = "Username of the user to retrieve", required = true) @PathVariable String username) {
        return userService.getUserByUsername(username);
    }
}