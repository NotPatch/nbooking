package com.notpatch.nbooking.service;

import com.notpatch.nbooking.dto.AuthResponse;
import com.notpatch.nbooking.dto.LoginRequest;
import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.model.Customer;
import com.notpatch.nbooking.repository.BusinessRepository;
import com.notpatch.nbooking.repository.CustomerRepository;
import com.notpatch.nbooking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse registerBusiness(Business business) {
        if (businessRepository.findByEmail(business.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + business.getEmail());
        }
        business.setPassword(passwordEncoder.encode(business.getPassword()));
        Business saved = businessRepository.save(business);
        String token = jwtUtil.generateToken(saved.getEmail(), "BUSINESS", saved.getId());
        return new AuthResponse(token, "BUSINESS", saved.getId());
    }

    public AuthResponse loginBusiness(LoginRequest request) {
        Business business = businessRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), business.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(business.getEmail(), "BUSINESS", business.getId());
        return new AuthResponse(token, "BUSINESS", business.getId());
    }

    public AuthResponse loginCustomer(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (customer.getPassword() == null || !passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(customer.getEmail(), "CUSTOMER", customer.getId());
        return new AuthResponse(token, "CUSTOMER", customer.getId());
    }
}
