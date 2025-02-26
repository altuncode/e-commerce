package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.ProductPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductPdfRepo")
public interface ProductPdfRepo extends JpaRepository<ProductPdf, Long> {

    @Query("SELECT i FROM ProductPdf i WHERE i.product.id = :productId")
    List<ProductPdf> findByProductId(Long productId);
}
