package com.naoki.investmentledger.controller;

import com.naoki.investmentledger.entity.User;
import com.naoki.investmentledger.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ユーザー登録 API
    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String rawPassword = request.get("password");

        // パスワードを暗号化して保存
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, encodedPassword);
        userRepository.save(user);

        return "User registered successfully!";
    }

    // ログイン API (簡易版)
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String rawPassword = request.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // パスワードが合っているかチェック
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return "Login Successful!";
        } else {
            throw new RuntimeException("Invalid password");
        }
    }

}
