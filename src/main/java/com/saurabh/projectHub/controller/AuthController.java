package com.saurabh.projectHub.controller;

import com.saurabh.projectHub.dto.AuthResponse;
import com.saurabh.projectHub.dto.LoginRequest;
import com.saurabh.projectHub.dto.RegisterRequest;
import com.saurabh.projectHub.dto.RegisterResponse;
import com.saurabh.projectHub.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.login(request);

            // Set JWT as HttpOnly cookie instead of returning it in body
            Cookie cookie = new Cookie("jwt", authResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);         // set false in local dev (no HTTPS)
            cookie.setPath("/");
            cookie.setMaxAge(86400);        // 1 day
            cookie.setAttribute("SameSite", "Strict"); // CSRF protection

            response.addCookie(cookie);

            // Don't expose token in body anymore
            return ResponseEntity.ok("Login successful");

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);   // ← immediately expire the cookie
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}