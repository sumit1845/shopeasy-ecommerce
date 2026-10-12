package com.shopeasy.controller;

import com.shopeasy.dto.AuthRequest;
import com.shopeasy.dto.AuthResponse;
import com.shopeasy.model.User;
import com.shopeasy.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.register(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getProfile(@RequestParam(required = false) String email,
                                                   @RequestParam(required = false) Long id) {
        Optional<User> userOpt = Optional.empty();
        if (id != null) {
            userOpt = authService.findById(id);
        } else if (email != null && !email.trim().isEmpty()) {
            userOpt = authService.findByEmail(email.trim());
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "User profile fetched",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getLoyaltyPoints(),
                    user.getCreatedAt().format(DateTimeFormatter.ofPattern("M/d/yyyy"))
            ));
        }

        return ResponseEntity.notFound().build();
    }
}
