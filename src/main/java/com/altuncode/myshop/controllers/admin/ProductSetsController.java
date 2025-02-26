package com.altuncode.myshop.controllers.admin;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.services.PhotoService;
import com.altuncode.myshop.services.ProductService;
import com.altuncode.myshop.services.ProductSetsService;
import com.altuncode.myshop.util.validator.ProductFileValidator;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller("ProductSetsController")
@RequestMapping("/admin/product/sets")
@PreAuthorize("hasRole('ADMIN')")
public class ProductSetsController {
    private final ProductSetsService productSetsService;
    private final PhotoService photoService;
    private final ProductFileValidator productFileValidator;
    private final ProductService productService;

    @Autowired
    public ProductSetsController(@Qualifier("ProductSetsService") ProductSetsService productSetsService, @Qualifier("PhotoService") PhotoService photoService, @Qualifier("ProductFileValidator") ProductFileValidator productFileValidator, @Qualifier("ProductService") ProductService productService) {
        this.productSetsService = productSetsService;
        this.photoService = photoService;
        this.productFileValidator = productFileValidator;
        this.productService = productService;
    }


    // Get all products
    @GetMapping({"", "/"})
    public String allGET(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<ProductSets> productSets = productSetsService.getAllProductSetsWithPage(PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("productSets", productSets);
        model.addAttribute("totalItems", productSets.getTotalElements());
        model.addAttribute("totalPage", productSets.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/product/sets/list";
    }

    // Add new product
    @GetMapping({"add", "add/"})
    public String addGET(Model model) {
        model.addAttribute("productSets", new ProductSets());
        model.addAttribute("availableProducts", productService.getAllActiveProductList());
        return "admin/product/sets/addedit";
    }

    //Save new product
    @PostMapping({"add","add/"})
    public String addPOST(@RequestParam(value = "associatedProductIds", required = false) List<Long> associatedProductIds, @ModelAttribute("productSets") @Valid ProductSets productSets, BindingResult bindingResult, @RequestParam(value = "imagess", required = false) List<MultipartFile> images, Model model) {
        //check validation
        productFileValidator.setProfuctFiles(new Photo());
        productFileValidator.validate(images, bindingResult);


        if(bindingResult.hasErrors()) {
            model.addAttribute("availableProducts", productService.getAllActiveProductList());
            return "admin/product/sets/addedit";
        }
        try {
            // Save product first to generate ID
            ProductSets savedProductSets = productSetsService.saveProductSets(productSets);
            productSetsService.addProductToSets(savedProductSets,associatedProductIds);

            // Save each image and associate with the book
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        ProductSetsPhoto image = photoService.savePhotoForProductSets(file);
                        savedProductSets.addPhoto(image);
                    }
                }
            }
            // Save book again with images
            productSetsService.saveProductSets(savedProductSets);
            return "redirect:/admin/product/sets";
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.rejectValue("images", "error.images",
                    "An error occurred while uploading files.");
            return "redirect:/admin/product/sets";
        }
    }

    // Update existing product
    @PatchMapping({"edit/{id}", "edit/{id}/"})
    public String updatePOST(@RequestParam(value = "associatedProductIds", required = false) List<Long> associatedProductIds,
                             @PathVariable("id") Long id,
                             @ModelAttribute("productSets") @Valid ProductSets productSets,
                             BindingResult bindingResult,
                             @RequestParam(value = "imagess", required = false) List<MultipartFile> images,
                             @RequestParam(value = "deletedImg", required = false) List<Long> deletedImgIds,
                             Model model) {
        //check validation
        productFileValidator.setProfuctFiles(new Photo());
        productFileValidator.validate(images, bindingResult);


        if (bindingResult.hasErrors()) {
            model.addAttribute("availableProducts", productService.getAllActiveProductList());
            return "admin/product/sets/addedit";
        }

        try {
            // Fetch existing product from the database
            ProductSets existingProductSets = productSetsService.getProductSetsById(id);

            if (existingProductSets == null) {
                return "redirect:/admin/product/sets"; // Redirect if product not found
            }

            productSetsService.addProductToSets(existingProductSets,associatedProductIds);


            //hange update img
            if(!productSets.getProductSetsPhotos().isEmpty()){
                for(ProductSetsPhoto photo: productSets.getProductSetsPhotos()){
                    photoService.updatePhotoDetailForProductSets(photo.getId(),photo.getOrderImg());
                }
            }

            // Handle deleted images
            if (deletedImgIds != null) {
                for (Long imgId : deletedImgIds) {
                    photoService.deletePhotoForProductSets(imgId);
                }
            }
            // Handle new images
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        ProductSetsPhoto image = photoService.savePhotoForProductSets(file);
                        existingProductSets.addPhoto(image);
                    }
                }
            }
            // Save updated product
            productSetsService.updateProduct(existingProductSets, productSets);
            return "redirect:/admin/product/sets";
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.rejectValue("images", "error.images",
                    "An error occurred while uploading files.");
            return "admin/product/sets/addedit";
        }
    }



    // Update product - Display the edit form
    @GetMapping({"edit/{id}", "edit/{id}/"})
    public String editGET(@PathVariable("id") Long id, Model model) {
        // Fetch the product by ID
        ProductSets productSets = productSetsService.getProductSetsById(id);
        if (productSets == null) {
            return "redirect:/admin/product/sets"; // Redirect if product not found
        }
        // Populate model attributes
        model.addAttribute("availableProducts", productService.getAllActiveProductList());
        model.addAttribute("productSets", productSets);
        return "admin/product/sets/addedit";
    }


    // Altun buna baxdi
    // Delete color
    @DeleteMapping({"delete/{id}","delete/{id}/"})
    public String deleteDELETE(@PathVariable("id") Long id) throws IOException {
        if (productSetsService.getProductSetsById(id) == null) {
            return "redirect:/admin/product/sets";
        }
        productSetsService.deleteProductSetsById(id);
        return "redirect:/admin/product/sets";
    }
}
