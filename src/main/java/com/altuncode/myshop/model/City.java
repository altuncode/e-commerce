package com.altuncode.myshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "City")
@Data
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "This field is required and cannot be blank.")
    private String name;

    @Column(name = "price", nullable = false)
    @NotNull(message = "Price is required.")
    private Double price = 0.0;
}
