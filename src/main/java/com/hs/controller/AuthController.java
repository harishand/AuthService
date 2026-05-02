package com.hs.controller;

import com.hs.dto.LoginRequest;
import com.hs.service.JwtService;
import com.hs.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // 🆕 Register
    @PostMapping("/register")
    public String register(@RequestBody LoginRequest req) {
        userService.register(req.getUsername(), req.getPassword());
        return "User Registered";
    }

    // 🔐 Login
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {

        userService.login(req.getUsername(), req.getPassword());

        return jwtService.generateToken(req.getUsername(), List.of("USER"));
    }
}
