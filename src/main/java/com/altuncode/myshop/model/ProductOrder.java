package com.altuncode.myshop.model;

import com.altuncode.myshop.model.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_order")
@Data
@NoArgsConstructor
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_amount")
    private double totalAmount;

    @CreationTimestamp // Automatically sets the creation date
    @Column(name = "create_date", updatable = false) // Prevent updates to this field
    private LocalDateTime createDate;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "address2")
    private String address2;

    @Column(name = "companyName")
    private String companyName;

    @Column(name = "city")
    private String city;

    @Column(name = "postalCode")
    private String postalCode;

    @Column(name = "pickup")
    private Boolean pickup;

    @Column(name = "note")
    private String note;

    // The user who placed the order
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = true)
    private OrderStatusEnum orderStatusEnum;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductOrderItem> productOrderItems = new ArrayList<>();



}
