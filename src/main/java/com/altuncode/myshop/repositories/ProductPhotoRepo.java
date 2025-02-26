package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductPhotoRepo")
public interface ProductPhotoRepo extends JpaRepository<ProductPhoto, Long> {

    @Query("SELECT p FROM ProductPhoto p WHERE p.product.id = :productId ORDER BY p.orderImg ASC")
    List<ProductPhoto> getProductPhotoByProductId(Long productId);

}
