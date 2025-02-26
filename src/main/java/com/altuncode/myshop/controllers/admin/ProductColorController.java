package com.altuncode.myshop.controllers.admin;

import com.altuncode.myshop.model.ProductColor;
import com.altuncode.myshop.services.ProductColorService;
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


@Controller("ProductColorController")
@RequestMapping("/admin/product/color")
@PreAuthorize("hasRole('ADMIN')")
public class ProductColorController {

    // Altun buna baxdi
    private final ProductColorService productColorService;

    // Altun buna baxdi
    @Autowired
    public ProductColorController(@Qualifier("ProductColorService") ProductColorService productColorService) {
        this.productColorService = productColorService;
    }

    // Altun buna baxdi
    //Get all color list
    @GetMapping({"list", "list/"})
    public String allGET(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<ProductColor> productColors = productColorService.getAllProductColors(PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("productColors", productColors);
        model.addAttribute("totalItems", productColors.getTotalElements());
        model.addAttribute("totalPage", productColors.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/product/color/list";
    }

    // Altun buna baxdi
    // Add new color
    @GetMapping({"add","add/"})
    public String addGET(Model model) {
        model.addAttribute("productColor", new ProductColor());
        return "admin/product/color/addedit";
    }

    // Altun buna baxdi
    //Save new color
    @PostMapping({"add","add/"})
    public String addPOST(@ModelAttribute("productColor") @Valid ProductColor productColor, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "admin/product/color/addedit";
        }
        productColorService.saveProductColor(productColor);
        return "redirect:/admin/product/color/list";
    }

    // Altun buna baxdi
    // Edit color
    @GetMapping({"edit/{id}","edit/{id}/"})
    public String editGET(@PathVariable("id") Long id, Model model) {
        ProductColor productColor = productColorService.getProductColorById(id);
        if (productColor == null) {
            return "redirect:/admin/product/color/list";
        }
        model.addAttribute("productColor", productColor);
        return "admin/product/color/addedit";
    }

    // Altun buna baxdi
    // Save edit color
    @PatchMapping({"edit/{id}","edit/{id}/"})
    public String editPATCH(@PathVariable("id") Long id, @ModelAttribute("productColor") @Valid ProductColor productColor, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "admin/product/color/addedit";
        }
        if (productColorService.getProductColorById(id) == null) {
            return "redirect:/admin/product/color/list";
        }
        productColor.setId(id);
        productColorService.saveProductColor(productColor);
        return "redirect:/admin/product/color/list";
    }

    // Altun buna baxdi
    // Delete color
    @DeleteMapping({"delete/{id}","delete/{id}/"})
    public String deleteDELETE(@PathVariable("id") Long id) {
        if (productColorService.getProductColorById(id) == null) {
            return "redirect:/admin/product/color/list";
        }
        productColorService.deleteProductColorById(id);
        return "redirect:/admin/product/color/list";
    }






}
