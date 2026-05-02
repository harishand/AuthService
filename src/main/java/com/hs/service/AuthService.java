package com.hs.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String login(String username, String password) {

        // 🔥 Replace with DB validation
        if ("harish".equals(username) && "1234".equals(password)) {
            return jwtService.generateToken(username, List.of("USER"));
        }

        throw new RuntimeException("Invalid credentials");
    }
}
