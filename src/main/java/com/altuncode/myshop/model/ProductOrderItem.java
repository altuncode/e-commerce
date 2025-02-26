package com.altuncode.myshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_order_item")
@Data
@NoArgsConstructor
public class ProductOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; //+

    @Column(name = "product_id")
    private String productid; //+

    @Column(name = "product_name")
    private String productName; //+

    @Column(name = "product_size")
    private String productSize; //+

    @Column(name = "product_color")
    private String productColor; //+

    @Column(name = "needs_installation")
    private Boolean needsInstallation; //+

    @Column(name = "price_product")
    private Double priceAtPurchase; //+

    @Column(name = "install_price_product")
    private Double installPriceAtPurchase; //+

    @Column(name = "quantity")
    private Integer quantity;
}
