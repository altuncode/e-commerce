package com.altuncode.myshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique=true)
    @Email
    private String email;

    @Column(name = "password", nullable=false)
    private String password;

    //forgotpassword
    @Column(name = "reset_password_code")
    private String resetPasswordCode;

    //forgotpassword
    @Column(name = "verification_code_expires_at_for_reset_password")
    private LocalDateTime verificationCodeExpiresAtForResetPassword;

    @Column(name = "role", nullable=false)
    private String role;

    @CreationTimestamp // Automatically sets the creation date
    @Column(name = "created_date", updatable = false) // Prevent updates to this field
    private LocalDateTime createDate;

    @Column(name = "blocked")
    private boolean blocked = false;
}
