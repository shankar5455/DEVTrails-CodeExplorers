package com.earnsafe.controller;

import com.earnsafe.dto.*;
import com.earnsafe.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DashboardService dashboardService;
    private final ClaimService claimService;
    private final PolicyService policyService;
    private final ParametricTriggerService triggerService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> adminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/claims")
    public ResponseEntity<List<ClaimResponse>> allClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @GetMapping("/policies")
    public ResponseEntity<List<PolicyResponse>> allPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @PostMapping("/trigger-check")
    public ResponseEntity<String> triggerWeatherCheck() {
        triggerService.checkAndTriggerClaims();
        return ResponseEntity.ok("Weather check triggered successfully");
    }
}
