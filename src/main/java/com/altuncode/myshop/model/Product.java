package com.altuncode.myshop.model;

import com.altuncode.myshop.model.enums.ProductStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    //***************Variables***************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Product name is required and cannot be blank.")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters.")
    private String name;

    @Column(name = "url", unique = true)
    private String url;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "description_product_id", referencedColumnName = "id", nullable = false)
    private DescriptionProduct descriptionProduct;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "quantity", nullable = false)
    @Positive(message = "Quantity must be zero or positive.")
    @NotNull(message = "Quantity is required.")
    private Integer quantity;

    @Column(name = "price", nullable = false)
    @NotNull(message = "Price is required.")
    @Positive(message = "Price must be positive.")
    private Double price;

    @Column(name = "new_price")
    @Positive(message = "New price must be positive.")
    private Double newPrice;

    @Column(name = "install_price")
    @Min(message = "Install price must be positive.", value = 0)
    private Double installPrice =0.0;

    @Column(name = "width", nullable = false)
    @NotNull(message = "Width is required.")
    private Integer width;

    @Column(name = "height", nullable = false)
    @NotNull(message = "Height is required.")
    private Integer height;

    @Column(name = "depth", nullable = false)
    @NotNull(message = "Depth is required.")
    private Integer depth;

    @NotNull(message = "Active status is required.")
    @Column(name = "active", nullable = false)
    private Boolean active=true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_color_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ProductColor productColor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_sub_category_list",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "product_sub_category_id")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ProductSubCategory> productSubCategoryList;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ProductPhoto> photos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status_product")
    private ProductStatusEnum productStatusEnum;

    @Column(name = "created_date", nullable = false)
    @NotNull(message = "Created date is required.")
    @DateTimeFormat(pattern = "MM/dd/yyyy") // Match your input format
    private LocalDate createdDate;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ProductPdf> productPdfs = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_associations",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "associated_product_id")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Product> relatedProducts = new ArrayList<>();

    @ManyToMany(mappedBy = "productList", fetch = FetchType.LAZY)
    private List<ProductSets> productSets = new ArrayList<>();

    //***************Methods***************

    //add associated product
    public void addAssociatedProduct(Product otherProduct) {
        this.relatedProducts.add(otherProduct);
        otherProduct.getRelatedProducts().add(this);
    }

    //remove associated product
    public void removeAssociatedProduct(Product otherProduct) {
        this.relatedProducts.remove(otherProduct);
        otherProduct.getRelatedProducts().remove(this);
    }

    public void addPhoto(ProductPhoto productPhoto) {
        photos.add(productPhoto);
        productPhoto.setProduct(this);
    }

    public void addPdf(ProductPdf productPdf) {
        productPdfs.add(productPdf);
        productPdf.setProduct(this);
    }

    //check for thymlead that subcategories selected or not
    public boolean checkForMatchingId(int a) {
        if(productSubCategoryList==null || productSubCategoryList.isEmpty())
            return false;
        for (ProductSubCategory b : productSubCategoryList) {
            if (b.getId() == a) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}