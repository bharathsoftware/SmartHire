package com.smarthire.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smarthire.dto.LoginRequest;
import com.smarthire.entity.User;
import com.smarthire.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Create new user
    public User createUser(User user) {
        String email = user.getEmail() == null
                ? ""
                : user.getEmail().trim().toLowerCase();

        if (email.isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists. Please sign in instead.");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }
        if (user.getRole() == null) {
            throw new RuntimeException("Please select an account type");
        }

        user.setEmail(email);
        user.setFullName(user.getFullName().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    // Update user
    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setLocation(updatedUser.getLocation());
        existingUser.setRole(updatedUser.getRole());

        return userRepository.save(existingUser);
    }

    // Delete user
    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }

    // Login user and generate JWT token
    public String login(LoginRequest request) {

        String email = request.getEmail() == null
                ? ""
                : request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        return jwtService.generateToken(user);
    }
}