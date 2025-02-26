package com.altuncode.myshop.controllers.admin;

import com.altuncode.myshop.model.ProductSubCategory;
import com.altuncode.myshop.services.ProductCategoryService;
import com.altuncode.myshop.services.ProductSubCategoryService;
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

@Controller("ProductSubCategoryController")
@RequestMapping("/admin/product/subcategory")
@PreAuthorize("hasRole('ADMIN')")
public class ProductSubCategoryController {

    private final ProductSubCategoryService productSubCategoryService;
    private final ProductCategoryService productCategoryService;

    // Altun buna baxdi
    @Autowired
    public ProductSubCategoryController(@Qualifier("ProductSubCategoryService") ProductSubCategoryService productSubCategoryService, @Qualifier("ProductCategoryService") ProductCategoryService productCategoryService) {
        this.productSubCategoryService = productSubCategoryService;
        this.productCategoryService = productCategoryService;
    }

    // Altun buna baxdi
    // Get all subcategories with pagination
    @GetMapping({"list", "list/"})
    public String allGET(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<ProductSubCategory> subCategories = productSubCategoryService.getAllProductSubCategories(PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("subCategories", subCategories);
        model.addAttribute("totalItems", subCategories.getTotalElements());
        model.addAttribute("totalPage", subCategories.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/product/subcategory/list";
    }

    // Altun buna baxdi
    // Add new subcategory
    @GetMapping({"add", "add/"})
    public String addGET(Model model) {
        model.addAttribute("productSubCategory", new ProductSubCategory());
        model.addAttribute("productCategories", productCategoryService.getAllProductCategories()); // Fetch all categories
        return "admin/product/subcategory/addedit";
    }

    // Altun buna baxdi
    // Edit subcategory
    @GetMapping({"edit/{id}", "edit/{id}/"})
    public String editGET(@PathVariable("id") Long id, Model model) {
        ProductSubCategory productSubCategory = productSubCategoryService.getProductSubCategoryById(id);
        if (productSubCategory == null) {
            return "redirect:/admin/product/subcategory/list";
        }
        model.addAttribute("productSubCategory", productSubCategory);
        model.addAttribute("productCategories", productCategoryService.getAllProductCategories()); // Fetch all categories
        return "admin/product/subcategory/addedit";
    }

    // Altun buna baxdi
    // Save new subcategory
    @PostMapping({"add", "add/"})
    public String addPOST(@ModelAttribute("productSubCategory") @Valid ProductSubCategory productSubCategory, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productCategories", productCategoryService.getAllProductCategories()); // Fetch all categories
            return "admin/product/subcategory/addedit";
        }
        productSubCategoryService.saveProductSubCategory(productSubCategory);
        return "redirect:/admin/product/subcategory/list";
    }

    // Altun buna baxdi
    // Save edited subcategory
    @PatchMapping({"edit/{id}", "edit/{id}/"})
    public String editPATCH(@PathVariable("id") Long id, @ModelAttribute("productSubCategory") @Valid ProductSubCategory productSubCategory, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productCategoryService.getAllProductCategories());
            return "admin/product/subcategory/addedit";
        }
        ProductSubCategory existingSubCategory = productSubCategoryService.getProductSubCategoryById(id);
        if (existingSubCategory == null) {
            return "redirect:/admin/product/subcategory/list";
        }
        productSubCategory.setId(id);
        productSubCategoryService.saveProductSubCategory(productSubCategory);
        return "redirect:/admin/product/subcategory/list";
    }

    // Altun buna baxdi
    // Delete subcategory
    @DeleteMapping({"delete/{id}", "delete/{id}/"})
    public String deleteDELETE(@PathVariable("id") Long id) {
        ProductSubCategory productSubCategory = productSubCategoryService.getProductSubCategoryById(id);
        if (productSubCategory == null) {
            return "redirect:/admin/product/subcategory/list";
        }
        productSubCategoryService.deleteProductSubCategoryById(id);
        return "redirect:/admin/product/subcategory/list";
    }
}
