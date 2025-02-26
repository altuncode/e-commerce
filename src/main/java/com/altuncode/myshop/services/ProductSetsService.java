package com.altuncode.myshop.services;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.repositories.ProductSetsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("ProductSetsService")
@Transactional(readOnly = true)
public class ProductSetsService {

    private final ProductSetsRepo productSetsRepo;
    private final PhotoService photoService;
    private final ProductService productService;

    @Autowired
    public ProductSetsService(@Qualifier("ProductSetsRepo") ProductSetsRepo productSetsRepo, @Qualifier("PhotoService") PhotoService photoService, @Qualifier("ProductService") ProductService productService) {
        this.productSetsRepo = productSetsRepo;
        this.photoService = photoService;
        this.productService = productService;
    }

    public Page<ProductSets> getAllProductSetsWithPage(Pageable pageable) {
        return productSetsRepo.findAll(pageable);
    }


    public ProductSets getProductSetsById(Long id) {
        return productSetsRepo.findById(id).orElse(null);
    }



    //Save a product
    @Transactional
    public ProductSets saveProductSets(ProductSets productSets) {
        ProductSets savedProductSets = productSetsRepo.save(productSets);
        savedProductSets.setUrl(generateUrl(savedProductSets));
        savedProductSets.updatePrices();
        return productSetsRepo.save(savedProductSets);
    }


    //update existing product without save just send to saveProduct method
    @Transactional
    public void updateProduct(ProductSets existingProductSets, ProductSets newProductSets) {
        existingProductSets.setName(newProductSets.getName());
        existingProductSets.setDescription(newProductSets.getDescription());
        existingProductSets.setShortDescription(newProductSets.getShortDescription());
        existingProductSets.setActive(newProductSets.getActive());
        existingProductSets.setCreatedDate(newProductSets.getCreatedDate());
        saveProductSets(existingProductSets);
    }




    //delete product by id
    @Transactional
    public void deleteProductSetsById(Long id) throws IOException {
        ProductSets productSets = getProductSetsById(id);
        if(productSets == null) {
            return;
        }
        if(productSets.getProductSetsPhotos() != null && productSets.getProductSetsPhotos().size() > 0) {
            for (ProductSetsPhoto photo : productSets.getProductSetsPhotos()) {
                photoService.deletePhotoForProductSets(photo.getId());
            }
        }
        productSetsRepo.deleteById(id);
    }

    @Transactional
    public void addProductToSets(ProductSets productSets, List<Long> relatedProductIds) {
        if(relatedProductIds != null && relatedProductIds.size() > 0) {
            productSets.setProductList(productService.getProductListByIds(relatedProductIds));
        }
    }



    private String generateUrl(ProductSets productSets) {
        String orginalName = productSets.getName();
        String newName = orginalName.trim().toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-").replace("--","-");
        return newName + "-" + productSets.getId();
    }
}
