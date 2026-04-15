package com.earnsafe.controller;

import com.earnsafe.dto.ClaimResponse;
import com.earnsafe.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @GetMapping("/my")
    public ResponseEntity<List<ClaimResponse>> myClaims(Authentication auth) {
        return ResponseEntity.ok(claimService.getUserClaims(auth.getName()));
    }
}
