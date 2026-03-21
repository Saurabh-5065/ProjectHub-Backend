package com.saurabh.projectHub.service;

import com.saurabh.projectHub.dto.AuthResponse;
import com.saurabh.projectHub.dto.LoginRequest;
import com.saurabh.projectHub.dto.RegisterRequest;
import com.saurabh.projectHub.dto.RegisterResponse;
import com.saurabh.projectHub.model.User;
import com.saurabh.projectHub.repository.UserRepository;
import com.saurabh.projectHub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public AuthResponse login(  LoginRequest request) {
        // This will throw BadCredentialsException if invalid
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(auth.getName());
        return new AuthResponse(token, expirationMs);
    }

    public RegisterResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exist");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new RegisterResponse(
                "User registered successfully",
                user.getUsername(),
                user.getEmail()
        );
    }
}
