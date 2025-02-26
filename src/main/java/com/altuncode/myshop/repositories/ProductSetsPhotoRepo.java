package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.ProductPhoto;
import com.altuncode.myshop.model.ProductSetsPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductSetsPhotoRepo")
public interface ProductSetsPhotoRepo extends JpaRepository<ProductSetsPhoto, Long> {

    @Query("SELECT p FROM ProductSetsPhoto p WHERE p.productSets.id = :productSetsId ORDER BY p.orderImg ASC")
    List<ProductSetsPhoto> getProductSetsPhotoByProductSetsId(Long productSetsId);

}
