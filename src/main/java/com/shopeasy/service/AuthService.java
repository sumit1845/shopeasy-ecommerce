package com.shopeasy.service;

import com.shopeasy.dto.AuthRequest;
import com.shopeasy.dto.AuthResponse;
import com.shopeasy.model.User;
import com.shopeasy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return new AuthResponse(false, "Email is required.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return new AuthResponse(false, "Password must contain at least 6 characters.");
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            return new AuthResponse(false, "An account with this email already exists.");
        }

        String hashedPassword = hashPassword(request.getPassword());
        User user = new User(
                request.getName() != null ? request.getName().trim() : "Shopper",
                request.getEmail().trim().toLowerCase(),
                hashedPassword
        );
        User saved = userRepository.save(user);

        return new AuthResponse(
                true,
                "Account created successfully!",
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getLoyaltyPoints(),
                saved.getCreatedAt().format(DateTimeFormatter.ofPattern("M/d/yyyy"))
        );
    }

    public AuthResponse login(AuthRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return new AuthResponse(false, "Email and password are required.");
        }

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail().trim());
        if (userOpt.isEmpty()) {
            return new AuthResponse(false, "No account found. Please create an account first.");
        }

        User user = userOpt.get();
        String hashedPassword = hashPassword(request.getPassword());
        if (!user.getPassword().equals(hashedPassword)) {
            return new AuthResponse(false, "Invalid email or password.");
        }

        return new AuthResponse(
                true,
                "Login successful!",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLoyaltyPoints(),
                user.getCreatedAt().format(DateTimeFormatter.ofPattern("M/d/yyyy"))
        );
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
