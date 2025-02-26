package com.altuncode.myshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_sets")
@Data
@NoArgsConstructor
public class ProductSets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name is required and cannot be blank.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "color", nullable = false)
    @NotBlank(message = "Color is required and cannot be blank.")
    @Size(min = 2, max = 100, message = "Color must be between 2 and 100 characters.")
    private String color;

    @Column(name = "price")
    private Double price;

    @NotNull(message = "Active status is required.")
    @Column(name = "active", nullable = false)
    private Boolean active=true;

    @Column(name = "created_date", nullable = false)
    @NotNull(message = "Created date is required.")
    @DateTimeFormat(pattern = "MM/dd/yyyy") // Match your input format
    private LocalDate createdDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_sets_products",
            joinColumns = @JoinColumn(name = "product_set_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Product> productList = new ArrayList<>();

    @OneToMany(mappedBy = "productSets", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ProductSetsPhoto> productSetsPhotos = new ArrayList<>();


    public void updatePrices() {
        this.price = productList.stream()
                .mapToDouble(product -> {
                    if (product.getNewPrice() != null && product.getNewPrice() > 0) {
                        return product.getNewPrice(); // Use newPrice if available and valid
                    }
                    return product.getPrice(); // Fallback to price
                })
                .sum();
    }

    public void addPhoto(ProductSetsPhoto productSetsPhoto) {
        productSetsPhotos.add(productSetsPhoto);
        productSetsPhoto.setProductSets(this);
    }
}
