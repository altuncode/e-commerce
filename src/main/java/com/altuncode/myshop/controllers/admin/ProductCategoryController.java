package com.altuncode.myshop.controllers.admin;

import com.altuncode.myshop.model.ProductCategory;
import com.altuncode.myshop.services.ProductCategoryService;
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

@Controller("ProductCategoryController")
@RequestMapping("/admin/product/category")
@PreAuthorize("hasRole('ADMIN')")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    // Altun buna baxdi
    @Autowired
    public ProductCategoryController(@Qualifier("ProductCategoryService") ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    // Altun buna baxdi
    // Get all categories
    @GetMapping({"list", "list/"})
    public String allGET(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<ProductCategory> productCategories = productCategoryService.getAllProductCategories(PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("productCategories", productCategories);
        model.addAttribute("totalItems", productCategories.getTotalElements());
        model.addAttribute("totalPage", productCategories.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/product/category/list";
    }

    // Altun buna baxdi
    // Add new category
    @GetMapping({"add", "add/"})
    public String addGET(Model model) {
        model.addAttribute("productCategory", new ProductCategory());
        return "admin/product/category/addedit";
    }

    // Edit category
    @GetMapping({"edit/{id}", "edit/{id}/"})
    public String editGET(@PathVariable("id") Long id, Model model) {
        ProductCategory productCategory = productCategoryService.getProductCategoryById(id);
        if (productCategory == null) {
            return "redirect:/admin/product/category/list";
        }
        model.addAttribute("productCategory", productCategory);
        return "admin/product/category/addedit";
    }

    // Save new category
    @PostMapping({"add", "add/"})
    public String addPOST(@ModelAttribute("productCategory") @Valid ProductCategory productCategory, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/product/category/addedit";
        }
        productCategoryService.saveProductCategory(productCategory);
        return "redirect:/admin/product/category/list";
    }

    // Save edited category
    @PatchMapping({"edit/{id}", "edit/{id}/"})
    public String editPATCH(@PathVariable("id") Long id, @ModelAttribute("productCategory") @Valid ProductCategory productCategory, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/product/category/addedit";
        }
        if (productCategoryService.getProductCategoryById(id) == null) {
            return "redirect:/admin/product/category/list";
        }
        productCategory.setId(id);
        productCategoryService.saveProductCategory(productCategory);
        return "redirect:/admin/product/category/list";
    }

    // Delete category
    @DeleteMapping({"delete/{id}", "delete/{id}/"})
    public String deleteDELETE(@PathVariable("id") Long id) {
        if (productCategoryService.getProductCategoryById(id) == null) {
            return "redirect:/admin/product/category/list";
        }
        productCategoryService.deleteProductCategoryById(id);
        return "redirect:/admin/product/category/list";
    }

}
