package com.earnsafe.service;

import com.earnsafe.dto.*;
import com.earnsafe.entity.User;
import com.earnsafe.enums.Role;
import com.earnsafe.exception.BadRequestException;
import com.earnsafe.repository.UserRepository;
import com.earnsafe.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .city(request.getCity())
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .userId(user.getId())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .userId(user.getId())
                .build();
    }
}
