package com.altuncode.myshop.services;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.repositories.CityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service("CityService")
@Transactional(readOnly = true)
public class CityService {
    private final CityRepo cityRepo;

    @Autowired
    public CityService(@Qualifier("CityRepo") CityRepo cityRepo) {
        this.cityRepo = cityRepo;
    }

    // List all city with pageable
    public Page<City> getAllCityListWithPage(Pageable pageable) {
        return cityRepo.findAll(pageable);
    }

    // List all city without pageable
    public List<City> getAllCityList(Sort sort) {
        return cityRepo.findAll(sort);
    }

    public City getByName(String name){
        return cityRepo.findByName(name).orElse(null);
    }

    // Retrieve city by ID
    public City getCityById(Long id) {
        return cityRepo.findById(id).orElse(null);
    }

    //Save a product
    @Transactional
    public City saveCity(City city) {
        return cityRepo.save(city);
    }

    @Transactional
    public void deleteProductById(Long id) {
        City city = getCityById(id);
        if(city == null) {
            return;
        }
        cityRepo.deleteById(id);
    }
}
