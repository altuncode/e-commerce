package com.altuncode.myshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PRODUCTSETS")
@Data
@NoArgsConstructor
public class ProductSetsPhoto extends Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "productsets_id", referencedColumnName = "id")
    private ProductSets productSets;

}
