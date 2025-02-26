package com.altuncode.myshop.controllers.admin;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.model.enums.ProductStatusEnum;
import com.altuncode.myshop.services.*;
import com.altuncode.myshop.util.validator.ProductFileValidator;
import com.altuncode.myshop.util.validator.ProductValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.Duration;
import java.util.List;

@Controller("ProductController")
@RequestMapping("/admin/product/")
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {

    private final ProductService productService;
    private final ProductColorService productColorService;
    private final ProductSubCategoryService productSubCategoryService;
    private final ProductCategoryService productCategoryService;
    private final PhotoService photoService;
    private final ProductPdfService productPdfService;
    private final ProductValidator productValidator;
    private final ProductFileValidator productFileValidator;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(@Qualifier("ProductService") ProductService productService, @Qualifier("ProductColorService") ProductColorService productColorService, @Qualifier("ProductSubCategoryService") ProductSubCategoryService productSubCategoryService, @Qualifier("ProductCategoryService") ProductCategoryService productCategoryService, @Qualifier("PhotoService") PhotoService photoService, @Qualifier("ProductPdfService") ProductPdfService productPdfService, @Qualifier("ProductValidator") ProductValidator productValidator, @Qualifier("ProductFileValidator") ProductFileValidator productFileValidator) {
        this.productService = productService;
        this.productColorService = productColorService;
        this.productSubCategoryService = productSubCategoryService;
        this.productCategoryService = productCategoryService;
        this.photoService = photoService;
        this.productPdfService = productPdfService;
        this.productValidator = productValidator;
        this.productFileValidator = productFileValidator;
    }



    // Get all products
    @GetMapping({"list", "list/"})
    public String allGET(Model model, @RequestParam(defaultValue = "1") int page) {

        Page<Product> products = productService.getAllProductList(PageRequest.of(page-1, 10,Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("products", products);
        model.addAttribute("totalItems", products.getTotalElements());
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
//        logger.warn("This is a debug message");
//        logger.error("This is an error message");
        return "admin/product/list";
    }

    // Add new product
    @GetMapping({"add", "add/"})
    public String addGET(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productCategoryService.getAllCategoriesWithSubCategories());
        model.addAttribute("colors", productColorService.findAll());
        model.addAttribute("statuses", ProductStatusEnum.values());
        model.addAttribute("availableProducts", productService.getAllActiveProductList());
        return "admin/product/addedit";
    }

    // Update existing product
    @PatchMapping({"edit/{id}", "edit/{id}/"})
    public String updatePOST(@PathVariable("id") Long id,
                             @ModelAttribute("product") @Valid Product product,
                             BindingResult bindingResult,
                             @RequestParam(value = "imagess", required = false) List<MultipartFile> images,
                             @RequestParam(value = "pdfs", required = false) List<MultipartFile> pdfs,
                             @RequestParam(value = "deletedImg", required = false) List<Long> deletedImgIds,
                             @RequestParam(value = "deletedPdf", required = false) List<Long> deletePdf,
                             @RequestParam(value = "subCategoryIds", required = false) List<Long> subCategoryIds, @RequestParam(value = "associatedProductIds", required = false) List<Long> associatedProductIds, Model model) {
        //check validation
        productValidator.validate(product, bindingResult);
        productFileValidator.setProfuctFiles(new Photo());
        productFileValidator.validate(images, bindingResult);
        productFileValidator.setProfuctFiles(new ProductPdf());
        productFileValidator.validate(pdfs, bindingResult);

        if (subCategoryIds != null && subCategoryIds.size() > 0) {
            List<ProductSubCategory> subCategories = productSubCategoryService.findSubCategoriesByIds(subCategoryIds);
            product.setProductSubCategoryList(subCategories);
        } else {
            model.addAttribute("catCheck", true);
        }


        if (bindingResult.hasErrors() || model.containsAttribute("catCheck")) {
            model.addAttribute("categories", productCategoryService.getAllCategoriesWithSubCategories());
            model.addAttribute("colors", productColorService.findAll());
            model.addAttribute("statuses", ProductStatusEnum.values());
            model.addAttribute("availableProducts", productService.getAllActiveProductList());
            return "admin/product/addedit";
        }

        try {
            // Fetch existing product from the database
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return "redirect:/admin/product/list"; // Redirect if product not found
            }
            productService.linkRelatedProducts(existingProduct, associatedProductIds);




            //hange update pdf
            if(!product.getProductPdfs().isEmpty()){
                for(ProductPdf pdf: product.getProductPdfs()){
                    productPdfService.updatePdf(pdf.getId(),pdf.getAltText(),pdf.getOrderPdf());
                }
            }


            // Handle deleted pdf
            if (deletePdf != null) {
                for (Long pdfId : deletePdf) {
                    productPdfService.deletePdf(pdfId);
                }
            }

            // Handle new pdf
            if (pdfs != null) {
                for (MultipartFile pdf : pdfs) {
                    if (!pdf.isEmpty()) {
                        ProductPdf productPdf = productPdfService.savePdf(pdf);
                        existingProduct.addPdf(productPdf);
                    }
                }
            }


            //hange update img
            if(!product.getPhotos().isEmpty()){
                for(ProductPhoto photo: product.getPhotos()){
                    photoService.updatePhotoDetailForProduct(photo.getId(),photo.getOrderImg());
                }
            }

            // Handle deleted images
            if (deletedImgIds != null) {
                for (Long imgId : deletedImgIds) {
                    photoService.deletePhotoForProduct(imgId);
                }
            }
            // Handle new images
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        ProductPhoto image = photoService.savePhotoForProduct(file);
                        existingProduct.addPhoto(image);
                    }
                }
            }
            // Save updated product
            productService.updateProduct(existingProduct, product);
            return "redirect:/admin/product/list";
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.rejectValue("images", "error.images",
                    "An error occurred while uploading files.");
            return "admin/product/addedit";
        }
    }

    //Save new product
    @PostMapping({"add","add/"})
    public String addPOST( @ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @RequestParam(value = "imagess", required = false) List<MultipartFile> images, @RequestParam(value = "pdfs", required = false) List<MultipartFile> pdfs, @RequestParam(value = "subCategoryIds", required = false) List<Long> subCategoryIds, @RequestParam(value = "associatedProductIds", required = false) List<Long> associatedProductIds, Model model) {

        //check validation
        productValidator.validate(product, bindingResult);
        productFileValidator.setProfuctFiles(new Photo());
        productFileValidator.validate(images, bindingResult);
        productFileValidator.setProfuctFiles(new ProductPdf());
        productFileValidator.validate(pdfs, bindingResult);


        if(subCategoryIds != null && subCategoryIds.size() > 0) {
            List<ProductSubCategory> subCategories = productSubCategoryService.findSubCategoriesByIds(subCategoryIds);
            product.setProductSubCategoryList(subCategories);
        }else {
            model.addAttribute("catCheck", true);
        }


        if(bindingResult.hasErrors() || model.containsAttribute("catCheck")) {
            model.addAttribute("categories", productCategoryService.getAllCategoriesWithSubCategories());
            model.addAttribute("colors", productColorService.findAll());
            model.addAttribute("statuses", ProductStatusEnum.values());
            model.addAttribute("availableProducts", productService.getAllActiveProductList());
            return "admin/product/addedit";
        }



        try {
            // Save product first to generate ID
            Product savedProduct = productService.saveProduct(product);
            productService.linkRelatedProducts(savedProduct, associatedProductIds);

            // Save each image and associate with the book
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        ProductPhoto image = photoService.savePhotoForProduct(file);
                        savedProduct.addPhoto(image);
                    }
                }
            }
            // Save each pdf and associate with the book
            if (pdfs != null) {
                for (MultipartFile pdf : pdfs) {
                    if (!pdf.isEmpty()) {
                        ProductPdf productPdf = productPdfService.savePdf(pdf);
                        savedProduct.addPdf(productPdf);
                    }
                }
            }
            // Save book again with images
            productService.saveProduct(savedProduct);
            return "redirect:/admin/product/list";
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.rejectValue("images", "error.images",
                    "An error occurred while uploading files.");
            return "redirect:/admin/product/color/list";
        }
    }

    // Update product - Display the edit form
    @GetMapping({"edit/{id}", "edit/{id}/"})
    public String editGET(@PathVariable("id") Long id, Model model) {
        // Fetch the product by ID
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/product/list"; // Redirect if product not found
        }
        // Populate model attributes
        model.addAttribute("product", product);
        model.addAttribute("categories", productCategoryService.getAllCategoriesWithSubCategories());
        model.addAttribute("colors", productColorService.findAll());
        model.addAttribute("statuses", ProductStatusEnum.values());
        model.addAttribute("availableProducts", productService.getAllActiveProductList());
        return "admin/product/addedit";
    }

    // Altun buna baxdi
    // get one product
    @GetMapping({"view/{id}", "view/{id}/"})
    public String oneProductGET(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/product/list"; // Redirect if product not found
        }
        model.addAttribute("product", product);
        return "admin/product/oneProduct";
    }


    // Altun buna baxdi
    // Delete color
    @DeleteMapping({"delete/{id}","delete/{id}/"})
    public String deleteDELETE(@PathVariable("id") Long id) throws IOException {
        if (productService.getProductById(id) == null) {
            return "redirect:/admin/product/list";
        }
        productService.deleteProductById(id);
        return "redirect:/admin/product/list";
    }

}
