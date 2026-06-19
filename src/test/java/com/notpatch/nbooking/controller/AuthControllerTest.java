package com.notpatch.nbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notpatch.nbooking.dto.AuthResponse;
import com.notpatch.nbooking.dto.LoginRequest;
import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.security.JwtAuthenticationFilter;
import com.notpatch.nbooking.security.JwtUtil;
import com.notpatch.nbooking.security.SecurityConfig;
import com.notpatch.nbooking.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@EnableWebSecurity
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void registerBusinessReturns201() throws Exception {
        when(authService.registerBusiness(any(Business.class)))
                .thenReturn(new AuthResponse("token-123", "BUSINESS", 1L));

        mockMvc.perform(post("/api/auth/business/register")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(validBusiness())))
                .andExpect(status().isCreated());
    }

    @Test
    void loginBusinessWithBadCredentialsReturns401() throws Exception {
        when(authService.loginBusiness(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/business/login")
                        .contentType("application/json")
                        .content("{\"email\":\"owner@business.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    private Business validBusiness() {
        Business business = new Business();
        business.setBusinessName("Joe's Barbershop");
        business.setEmail("owner@business.com");
        business.setPassword("plaintext");
        return business;
    }
}
