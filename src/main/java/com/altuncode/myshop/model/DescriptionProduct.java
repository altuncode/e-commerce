package com.altuncode.myshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "description_product")
@Getter
@Setter
@NoArgsConstructor
public class DescriptionProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "descriptionProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Product product;

    @Column(name = "description", columnDefinition = "TEXT")
    private String detail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DescriptionProduct that = (DescriptionProduct) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
