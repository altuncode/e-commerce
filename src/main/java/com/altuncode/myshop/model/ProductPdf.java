package com.altuncode.myshop.model;

import com.altuncode.myshop.model.interfaces.IProfuctFiles;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "product_pdf")
@Data
@NoArgsConstructor
public class ProductPdf implements IProfuctFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_pdf", nullable = true)
    private Integer orderPdf;

    @Column(name = "alt_text", nullable = true)
    private String altText;

    @Column(name = "url", nullable = false,unique = true)
    private String url;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Override
    public List<String> getAllowedExtensions() {
        return Arrays.asList("pdf");
    }

    @Override
    public List<String> getAllowedContentTypes() {
        return Arrays.asList("application/pdf");
    }
}
