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
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves a user by their username.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public Optional<User> getUser(
            @Parameter(description = "Username of the user to retrieve", required = true) @PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/check/{id}") // Matches the path /user/{id}
    @Operation(summary = "Check if user exists by ID", description = "Checks if a user exists for the given numeric ID. Returns 200 OK if exists, 404 Not Found otherwise. Used for validation.")
    @ApiResponse(responseCode = "200", description = "User exists")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> checkUserExistsById(
            @Parameter(description = "Numeric ID of the user to check", required = true) @PathVariable Long id) {

        boolean exists = userService.userExistsById(id); // Use the new service method

        if (exists) {
            return ResponseEntity.ok().build(); // HTTP 200 OK
        } else {
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        }
    }

    @GetMapping("/id/{id}") // Use a distinct path like /id/{id} to avoid ambiguity
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their numeric ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserById( // Return ResponseEntity<User>
                                             @Parameter(description = "Numeric ID of the user to retrieve", required = true) @PathVariable Long id) {
        return userService.getUserById(id) // Use the new service method
                .map(ResponseEntity::ok) // If found, wrap in ResponseEntity.ok()
                .orElseGet(() -> ResponseEntity.notFound().build()); // If not found, return 404
    }
}