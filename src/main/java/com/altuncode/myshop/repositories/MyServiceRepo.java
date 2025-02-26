package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.MyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MyServiceRepo")
public interface MyServiceRepo extends JpaRepository<MyService, Long> {

    // Altun buna baxdi+
    @Query("SELECT p FROM MyService p WHERE p.active = true ORDER BY p.createdDate DESC")
    List<MyService> findAllActiveMyService();
}
