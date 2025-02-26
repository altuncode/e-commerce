package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.Product;
import com.altuncode.myshop.model.ProductSets;
import com.altuncode.myshop.repositories.projection.ProductSetsProjectionUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("ProductSetsRepo")
public interface ProductSetsRepo extends JpaRepository<ProductSets, Long> {

    // Altun buna baxdi+
    @Query("SELECT p.id as id, p.name as name, p.url as url, " +
            "p.price as price, p.createdDate AS createdDate, " +
            "pp1.url AS firstPhotoUrl, pp2.url AS secondPhotoUrl FROM ProductSets p " +
            "LEFT JOIN p.productSetsPhotos pp1 ON pp1.id = (SELECT MIN(pp.id) FROM ProductSetsPhoto pp WHERE pp.productSets.id = p.id) " +
            "LEFT JOIN p.productSetsPhotos pp2 ON pp2.id = (SELECT MIN(pp.id) FROM ProductSetsPhoto pp WHERE pp.productSets = p AND pp.id > (SELECT MIN(pp.id) FROM ProductSetsPhoto pp WHERE pp.productSets.id = p.id )) " +
            "WHERE p.active = true ")
    Page<ProductSetsProjectionUser> findAllActiveProductSets(Pageable pageable);

    ProductSets findByUrlAndActiveTrue(String url);
}
