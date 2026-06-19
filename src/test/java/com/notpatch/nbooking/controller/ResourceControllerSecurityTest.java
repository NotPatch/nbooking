package com.notpatch.nbooking.controller;

import com.notpatch.nbooking.security.JwtAuthenticationFilter;
import com.notpatch.nbooking.security.JwtUtil;
import com.notpatch.nbooking.security.SecurityConfig;
import com.notpatch.nbooking.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@EnableWebSecurity
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class})
class ResourceControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @Test
    void createResourceWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/businesses/1/resources")
                        .contentType("application/json")
                        .content("{\"name\":\"Chair 1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createResourceWithCustomerRoleReturns403() throws Exception {
        mockMvc.perform(post("/api/businesses/1/resources")
                        .contentType("application/json")
                        .content("{\"name\":\"Chair 1\"}")
                        .with(user("customer@example.com").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }
}
