package com.earnsafe.config;

import com.earnsafe.entity.User;
import com.earnsafe.enums.Role;
import com.earnsafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .fullName("Admin User")
                    .email("admin@earnsafe.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("+91-9876543210")
                    .latitude(12.9716)
                    .longitude(77.5946)
                    .city("Bangalore")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created: admin@earnsafe.com / admin123");

            User worker1 = User.builder()
                    .fullName("Rahul Kumar")
                    .email("rahul@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .phone("+91-9876543211")
                    .latitude(19.0760)
                    .longitude(72.8777)
                    .city("Mumbai")
                    .role(Role.USER)
                    .build();
            userRepository.save(worker1);

            User worker2 = User.builder()
                    .fullName("Priya Sharma")
                    .email("priya@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .phone("+91-9876543212")
                    .latitude(28.7041)
                    .longitude(77.1025)
                    .city("Delhi")
                    .role(Role.USER)
                    .build();
            userRepository.save(worker2);

            User worker3 = User.builder()
                    .fullName("Amit Patel")
                    .email("amit@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .phone("+91-9876543213")
                    .latitude(13.0827)
                    .longitude(80.2707)
                    .city("Chennai")
                    .role(Role.USER)
                    .build();
            userRepository.save(worker3);

            log.info("Sample users created successfully");
        }
    }
}
