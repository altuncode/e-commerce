package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("ProductOrderRepo")
public interface ProductOrderRepo extends JpaRepository<ProductOrder, Long> {
    Page<ProductOrder> findAllByPersonId(Pageable pageable, Long userId);

    Page<ProductOrder> findByPerson_Email(Pageable pageable, String email);

    @Query("SELECT COUNT(p) FROM ProductOrder p")
    Long countProductOrders();

}
