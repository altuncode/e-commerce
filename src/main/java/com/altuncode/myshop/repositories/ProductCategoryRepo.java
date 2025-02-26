package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.ProductCategory;
import com.altuncode.myshop.repositories.projection.CategoryWithSubCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductCategoryRepo")
public interface ProductCategoryRepo  extends JpaRepository<ProductCategory, Long> {

    @Query("SELECT c FROM ProductCategory c LEFT JOIN FETCH c.subCategories")
    List<CategoryWithSubCategories> findAllCategoriesWithSubCategories();

    // bitdi 100%
    // Fetch only active categories, ordered by orderNumber
    List<ProductCategory> findByActiveTrueOrderByOrderNumberAsc();
}
