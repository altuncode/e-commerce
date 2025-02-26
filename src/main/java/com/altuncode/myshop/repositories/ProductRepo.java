package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.Product;
import com.altuncode.myshop.repositories.projection.ProductProjectionUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Repository("ProductRepo")
public interface ProductRepo extends JpaRepository<Product, Long> {

    // Derived query method
    List<Product> findByActiveTrue(Sort sort);

//    List<Product> findByIdIn(List<Long> ids);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Product p WHERE p.id = :productId AND p.active = TRUE")
    Boolean existsActiveProductById(@Param("productId") Long productId);


    Product findByUrlAndActiveTrue(String url);

    // Altun buna baxdi+
    @Query("SELECT p.quantity FROM Product p WHERE p.id = :productId")
    Optional<Integer> findQuantityByProductId(@Param("productId") Long productId);

    // Altun buna baxdi+
    @Query("SELECT p.id as id, p.name as name, p.url as url, " +
            "p.price as price, p.newPrice as newPrice, " +
            "p.productStatusEnum as productStatusEnum, p.createdDate AS createdDate, " +
            "pp1.url AS firstPhotoUrl, pp2.url AS secondPhotoUrl FROM Product p " +
            "LEFT JOIN p.photos pp1 ON pp1.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id) " +
            "LEFT JOIN p.photos pp2 ON pp2.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product = p AND pp.id > (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id )) " +
            "WHERE p.active = true ")
    Page<ProductProjectionUser> findAllActiveProducts(Pageable pageable);

    // search
    @Query("SELECT p.id as id, p.name as name, p.url as url, " +
            "p.price as price, p.newPrice as newPrice, " +
            "p.productStatusEnum as productStatusEnum, p.createdDate AS createdDate, " +
            "pp1.url AS firstPhotoUrl, pp2.url AS secondPhotoUrl FROM Product p " +
            "LEFT JOIN p.photos pp1 ON pp1.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id) " +
            "LEFT JOIN p.photos pp2 ON pp2.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product = p AND pp.id > (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id )) " +
            "WHERE p.active = true  AND  LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ProductProjectionUser> searchAllActiveProducts(@Param("name") String name, Pageable pageable);

    // Altun buna baxdi+
    @Query("SELECT p.id as id, p.name as name, p.url as url, " +
            "p.price as price, p.newPrice as newPrice, " +
            "p.productStatusEnum as productStatusEnum, p.createdDate AS createdDate, " +
            "pp1.url AS firstPhotoUrl, pp2.url AS secondPhotoUrl FROM Product p " +
            "LEFT JOIN p.photos pp1 ON pp1.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id) " +
            "LEFT JOIN p.photos pp2 ON pp2.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product = p AND pp.id > (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id )) " +
            "JOIN p.productSubCategoryList sc WHERE sc.productCategory.url = :categoryUrl AND p.active = true ")
    Page<ProductProjectionUser> findActiveProductsByCategoryUrl(String categoryUrl, Pageable pageable);

    // Altun buna baxdi+
    @Query("SELECT p.id as id, p.name as name, p.url as url, " +
            "p.price as price, p.newPrice as newPrice, " +
            "p.productStatusEnum as productStatusEnum, p.createdDate AS createdDate, " +
            "pp1.url AS firstPhotoUrl, pp2.url AS secondPhotoUrl FROM Product p " +
            "LEFT JOIN p.photos pp1 ON pp1.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id) " +
            "LEFT JOIN p.photos pp2 ON pp2.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product = p AND pp.id > (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id )) " +
            "JOIN p.productSubCategoryList sc WHERE sc.productCategory.url = :categoryUrl AND sc.url = :subCategoryUrl AND p.active = true ")
    Page<ProductProjectionUser> findActiveProductsByCategoryUrlAndSubCategoryUrl(String categoryUrl, String subCategoryUrl, Pageable pageable);

    // Altun buna baxdi+
    @Query("SELECT p.id as id, p.name as name, p.url as url, " +
            "CONCAT(p.width, 'x', p.height, 'x', p.depth, '\"') AS size, " +
            "p.productColor.name as color, p.price as price, p.newPrice as newPrice, " +
            "p.installPrice as installPrice, p.createdDate AS createdDate, " +
            "pp1.url AS firstPhotoUrl FROM Product p " +
            "LEFT JOIN p.photos pp1 ON pp1.id = (SELECT MIN(pp.id) FROM ProductPhoto pp WHERE pp.product.id = p.id) WHERE p.id IN :ids ")
    List<ProductProjectionUser> findProductsByIdsBaseOnCart(@Param("ids") List<Long> ids);


}
