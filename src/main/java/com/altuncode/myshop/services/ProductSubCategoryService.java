package com.altuncode.myshop.services;

import com.altuncode.myshop.model.ProductSubCategory;
import com.altuncode.myshop.repositories.ProductSubCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("ProductSubCategoryService")
@Transactional(readOnly = true)
public class ProductSubCategoryService {

    private final ProductSubCategoryRepo productSubCategoryRepo;

    @Autowired
    public ProductSubCategoryService(@Qualifier("ProductSubCategoryRepo") ProductSubCategoryRepo productSubCategoryRepo) {
        this.productSubCategoryRepo = productSubCategoryRepo;
    }

    // Get all subcategories with pagination
    public Page<ProductSubCategory> getAllProductSubCategories(Pageable pageable) {
        return productSubCategoryRepo.findAll(pageable);
    }

    // Get a single subcategory by ID
    public ProductSubCategory getProductSubCategoryById(Long id) {
        Optional<ProductSubCategory> productSubCategory = productSubCategoryRepo.findById(id);
        return productSubCategory.orElse(null);
    }

    @Transactional
    // Create or update a subcategory
    public ProductSubCategory saveProductSubCategory(ProductSubCategory productSubCategory) {
        productSubCategory.setUrl(generateUrl(productSubCategory));
        return productSubCategoryRepo.save(productSubCategory);
    }

    @Transactional
    // Delete a subcategory by ID
    public void deleteProductSubCategoryById(Long id) {
        productSubCategoryRepo.deleteById(id);
    }


    public List<ProductSubCategory> findSubCategoriesByIds(List<Long> subCategoryIds) {
        return productSubCategoryRepo.findAllById(subCategoryIds);
    }

    private String generateUrl(ProductSubCategory productSubCategory) {
        String orginalName = productSubCategory.getName();
        String newName = orginalName.trim().toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-").replace("--","-");
        return newName;
    }
}