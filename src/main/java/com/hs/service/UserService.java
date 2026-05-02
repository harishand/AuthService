package com.hs.service;

import com.hs.entity.User;
import com.hs.exception.CustomerServiceException;
import com.hs.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    // 🔐 Register user (basic validation + transactional)
    @Transactional
    public void register(String username, String password) {
        // Basic input validation
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new CustomerServiceException("Username and password must be provided");
        }

        String normalizedUsername = username.trim();

        // Optional: enforce minimal password length
        if (password.length() < 6) {
            throw new CustomerServiceException("Password must be at least 6 characters long");
        }

        // Check if user already exists
        if (repo.existsByUsername(normalizedUsername)) {
            throw new CustomerServiceException("Username already exists");
        }

        String encodedPassword = encoder.encode(password);

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPassword(encodedPassword);

        repo.save(user);
        log.info("Registered new user: {}", normalizedUsername);
    }

    // 🔑 Validate login - use consistent error messages to avoid user enumeration
    @Transactional(readOnly = true)
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new CustomerServiceException("Username and password must be provided");
        }

        String normalizedUsername = username.trim();

        User user = repo.findByUsername(normalizedUsername)
                .orElseThrow(() -> new CustomerServiceException("Invalid credentials"));

        if (!encoder.matches(password, user.getPassword())) {
            // don't reveal whether username exists
            throw new CustomerServiceException("Invalid credentials");
        }

        log.info("User logged in: {}", normalizedUsername);
        return user;
    }
}
