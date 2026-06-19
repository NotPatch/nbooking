package com.notpatch.nbooking.service;

import com.notpatch.nbooking.dto.AuthResponse;
import com.notpatch.nbooking.dto.LoginRequest;
import com.notpatch.nbooking.model.Business;
import com.notpatch.nbooking.model.Customer;
import com.notpatch.nbooking.repository.BusinessRepository;
import com.notpatch.nbooking.repository.CustomerRepository;
import com.notpatch.nbooking.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private BusinessRepository businessRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(businessRepository, customerRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void registerBusinessHashesPasswordAndReturnsToken() {
        Business business = new Business();
        business.setEmail("owner@business.com");
        business.setPassword("plaintext");

        when(businessRepository.findByEmail("owner@business.com")).thenReturn(Optional.empty());
        when(businessRepository.save(any(Business.class))).thenAnswer(invocation -> {
            Business saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(jwtUtil.generateToken("owner@business.com", "BUSINESS", 1L)).thenReturn("token-123");

        AuthResponse response = authService.registerBusiness(business);

        assertThat(response.getToken()).isEqualTo("token-123");
        assertThat(response.getRole()).isEqualTo("BUSINESS");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(business.getPassword()).isNotEqualTo("plaintext");
    }

    @Test
    void registerBusinessRejectsDuplicateEmail() {
        when(businessRepository.findByEmail("owner@business.com")).thenReturn(Optional.of(new Business()));

        Business business = new Business();
        business.setEmail("owner@business.com");
        business.setPassword("plaintext");

        assertThatThrownBy(() -> authService.registerBusiness(business))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loginBusinessSucceedsWithCorrectPassword() {
        Business business = new Business();
        business.setId(7L);
        business.setEmail("owner@business.com");
        business.setPassword(passwordEncoder.encode("correct-password"));

        when(businessRepository.findByEmail("owner@business.com")).thenReturn(Optional.of(business));
        when(jwtUtil.generateToken("owner@business.com", "BUSINESS", 7L)).thenReturn("token-456");

        LoginRequest request = new LoginRequest();
        request.setEmail("owner@business.com");
        request.setPassword("correct-password");

        AuthResponse response = authService.loginBusiness(request);

        assertThat(response.getToken()).isEqualTo("token-456");
    }

    @Test
    void loginBusinessRejectsWrongPassword() {
        Business business = new Business();
        business.setId(7L);
        business.setEmail("owner@business.com");
        business.setPassword(passwordEncoder.encode("correct-password"));

        when(businessRepository.findByEmail("owner@business.com")).thenReturn(Optional.of(business));

        LoginRequest request = new LoginRequest();
        request.setEmail("owner@business.com");
        request.setPassword("wrong-password");

        assertThatThrownBy(() -> authService.loginBusiness(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginBusinessRejectsUnknownEmail() {
        when(businessRepository.findByEmail("ghost@business.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@business.com");
        request.setPassword("anything");

        assertThatThrownBy(() -> authService.loginBusiness(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginCustomerRejectsCustomerWithNoPassword() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setEmail("guest@example.com");
        customer.setPassword(null);

        when(customerRepository.findByEmail("guest@example.com")).thenReturn(Optional.of(customer));

        LoginRequest request = new LoginRequest();
        request.setEmail("guest@example.com");
        request.setPassword("anything");

        assertThatThrownBy(() -> authService.loginCustomer(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginCustomerSucceedsWhenPasswordSetAndCorrect() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setEmail("member@example.com");
        customer.setPassword(passwordEncoder.encode("secret"));

        when(customerRepository.findByEmail("member@example.com")).thenReturn(Optional.of(customer));
        when(jwtUtil.generateToken("member@example.com", "CUSTOMER", 3L)).thenReturn("token-789");

        LoginRequest request = new LoginRequest();
        request.setEmail("member@example.com");
        request.setPassword("secret");

        AuthResponse response = authService.loginCustomer(request);

        assertThat(response.getToken()).isEqualTo("token-789");
        assertThat(response.getRole()).isEqualTo("CUSTOMER");
    }
}
