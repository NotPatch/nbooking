package com.notpatch.nbooking.service;

import com.notpatch.nbooking.model.Customer;
import com.notpatch.nbooking.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private PasswordEncoder passwordEncoder;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        customerService = new CustomerService(customerRepository, passwordEncoder);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void hashesPasswordWhenPresent() {
        Customer customer = new Customer();
        customer.setFirstName("Ada");
        customer.setLastName("Lovelace");
        customer.setPhone("5551234567");
        customer.setPassword("plaintext");

        Customer saved = customerService.create(customer);

        assertThat(saved.getPassword()).isNotEqualTo("plaintext");
        assertThat(passwordEncoder.matches("plaintext", saved.getPassword())).isTrue();
    }

    @Test
    void leavesPasswordNullWhenAbsent() {
        Customer customer = new Customer();
        customer.setFirstName("Guest");
        customer.setLastName("Walkin");
        customer.setPhone("5559876543");

        Customer saved = customerService.create(customer);

        assertThat(saved.getPassword()).isNull();
    }
}
