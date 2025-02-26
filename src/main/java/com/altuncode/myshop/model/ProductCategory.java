package com.altuncode.myshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "product_category")
@Data
@NoArgsConstructor
@ToString(exclude = "subCategories")
public class ProductCategory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "This field is required and cannot be blank.")
    @Size(min = 2, max = 50, message = "This field must be between 2 and 50 characters long.")
    private String name;

    @Column(name = "order_number")
    private Integer orderNumber;

    @NotNull(message = "Active status is required.")
    @Column(name = "active", nullable = false)
    private Boolean active=true;

    @Column(name = "url", unique = true)
    private String url;

    @OneToMany(mappedBy = "productCategory", fetch = FetchType.LAZY)
    private List<ProductSubCategory> subCategories;


}