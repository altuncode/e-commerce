package com.altuncode.myshop.services;

import com.altuncode.myshop.model.Product;
import com.altuncode.myshop.model.ProductCategory;
import com.altuncode.myshop.repositories.ProductCategoryRepo;
import com.altuncode.myshop.repositories.projection.CategoryWithSubCategories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("ProductCategoryService")
@Transactional(readOnly = true)
public class ProductCategoryService {

    private final ProductCategoryRepo productCategoryRepo;

    @Autowired
    public ProductCategoryService(@Qualifier("ProductCategoryRepo") ProductCategoryRepo productCategoryRepo) {
        this.productCategoryRepo = productCategoryRepo;
    }

    public List<ProductCategory> getAllProductCategories() {
        return productCategoryRepo.findAll();
    }

    public Page<ProductCategory> getAllProductCategories(Pageable pageable) {
        return productCategoryRepo.findAll(pageable);
    }

    public ProductCategory getProductCategoryById(Long id) {
        Optional<ProductCategory> productCategory = productCategoryRepo.findById(id);
        return productCategory.orElse(null);
    }

    public List<CategoryWithSubCategories> getAllCategoriesWithSubCategories() {
        return productCategoryRepo.findAllCategoriesWithSubCategories();
    }

    @Transactional
    public ProductCategory saveProductCategory(ProductCategory productCategory) {
        productCategory.setUrl(generateUrl(productCategory));
        return productCategoryRepo.save(productCategory);
    }

    @Transactional
    public void deleteProductCategoryById(Long id) {
        productCategoryRepo.deleteById(id);
    }

    private String generateUrl(ProductCategory productCategory) {
        String orginalName = productCategory.getName();
        String newName = orginalName.trim().toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-").replace("--","-");
        return newName;
    }
}
