package com.altuncode.myshop.controllers.admin;


import com.altuncode.myshop.model.City;
import com.altuncode.myshop.services.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller("CityController")
@RequestMapping("/admin/city")
@PreAuthorize("hasRole('ADMIN')")
public class CityController {
    private final CityService cityService;

    @Autowired
    public CityController(@Qualifier("CityService") CityService cityService) {
        this.cityService = cityService;
    }

    // Altun buna baxdi
    //Get all color list
    @GetMapping({"", "/"})
    public String allGET(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<City> cities = cityService.getAllCityListWithPage(PageRequest.of(page-1, 10, Sort.by(Sort.Direction.ASC, "name")));
        model.addAttribute("cities", cities);
        model.addAttribute("totalItems", cities.getTotalElements());
        model.addAttribute("totalPage", cities.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/product/city/list";
    }

    // Altun buna baxdi
    // Add new city
    @GetMapping({"add","add/"})
    public String addGET(Model model) {
        model.addAttribute("city", new City());
        return "admin/product/city/addedit";
    }

    // Altun buna baxdi
    //Save new city
    @PostMapping({"add","add/"})
    public String addPOST(@ModelAttribute("city") @Valid City city, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "admin/product/city/addedit";
        }
        cityService.saveCity(city);
        return "redirect:/admin/city";
    }

    // Altun buna baxdi
    // Edit city
    @GetMapping({"edit/{id}","edit/{id}/"})
    public String editGET(@PathVariable("id") Long id, Model model) {
        City city = cityService.getCityById(id);
        if (city == null) {
            return "redirect:/admin/city";
        }
        model.addAttribute("city", city);
        return "admin/product/city/addedit";
    }

    // Altun buna baxdi
    // Save edit color
    @PatchMapping({"edit/{id}","edit/{id}/"})
    public String editPATCH(@PathVariable("id") Long id, @ModelAttribute("city") @Valid City city, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "admin/product/city/addedit";
        }
        if (cityService.getCityById(id) == null) {
            return "redirect:/admin/city";
        }
        city.setId(id);
        cityService.saveCity(city);
        return "redirect:/admin/city";
    }

    // Altun buna baxdi
    // Delete city
    @DeleteMapping({"delete/{id}","delete/{id}/"})
    public String deleteDELETE(@PathVariable("id") Long id) {
        if (cityService.getCityById(id) == null) {
            return "redirect:/admin/city";
        }
        cityService.deleteProductById(id);
        return "redirect:/admin/city";
    }





}
