package com.earnsafe.controller;

import com.earnsafe.dto.*;
import com.earnsafe.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping("/purchase")
    public ResponseEntity<PolicyResponse> purchase(@RequestBody PolicyRequest request, Authentication auth) {
        return ResponseEntity.ok(policyService.purchasePolicy(auth.getName(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PolicyResponse>> myPolicies(Authentication auth) {
        return ResponseEntity.ok(policyService.getUserPolicies(auth.getName()));
    }

    @GetMapping("/active")
    public ResponseEntity<PolicyResponse> activePolicy(Authentication auth) {
        return ResponseEntity.ok(policyService.getActivePolicy(auth.getName()));
    }
}
