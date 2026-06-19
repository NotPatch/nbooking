package com.notpatch.nbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String businessName;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    private String address;
    private String phone;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
