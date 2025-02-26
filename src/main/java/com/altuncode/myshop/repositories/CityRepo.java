package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("CityRepo")
public interface CityRepo extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
}
