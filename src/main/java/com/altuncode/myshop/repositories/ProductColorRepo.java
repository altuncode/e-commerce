package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProductColorRepo")
public interface ProductColorRepo extends JpaRepository<ProductColor, Long> {

    @Query("SELECT p FROM ProductColor p ORDER BY p.id DESC")
    List<ProductColor> findAllOrderedByIdDesc();

}
