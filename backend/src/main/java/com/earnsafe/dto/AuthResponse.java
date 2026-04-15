package com.earnsafe.dto;

import com.earnsafe.enums.Role;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private Role role;
    private Long userId;
}
