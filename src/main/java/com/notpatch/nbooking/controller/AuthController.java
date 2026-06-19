package com.notpatch.nbooking.controller;

import com.notpatch.nbooking.dto.AuthResponse;
import com.notpatch.nbooking.dto.LoginRequest;
import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/business/register")
    public ResponseEntity<AuthResponse> registerBusiness(@RequestBody @Valid Business business) {
        return ResponseEntity.status(201).body(authService.registerBusiness(business));
    }

    @PostMapping("/business/login")
    public ResponseEntity<AuthResponse> loginBusiness(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.loginBusiness(request));
    }

    @PostMapping("/customer/login")
    public ResponseEntity<AuthResponse> loginCustomer(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.loginCustomer(request));
    }
}
