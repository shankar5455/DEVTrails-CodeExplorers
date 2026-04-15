package com.earnsafe.controller;

import com.earnsafe.dto.*;
import com.earnsafe.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<UserDashboard> userDashboard(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getUserDashboard(auth.getName()));
    }
}
