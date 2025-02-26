package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.ProductCategory;
import com.altuncode.myshop.model.ProductSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductSubCategoryRepo")
public interface ProductSubCategoryRepo extends JpaRepository<ProductSubCategory, Long> {

    // Fetch only active subcategories for a specific category, ordered by orderNumber
    List<ProductSubCategory> findByProductCategoryAndActiveTrueOrderByOrderNumberAsc(ProductCategory productCategory);
}