package com.altuncode.myshop.services;

import com.altuncode.myshop.model.ProductColor;
import com.altuncode.myshop.repositories.ProductColorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("ProductColorService")
@Transactional(readOnly = true)
public class ProductColorService {

    private final ProductColorRepo productColorRepo;

    @Autowired
    public ProductColorService(@Qualifier("ProductColorRepo") ProductColorRepo productColorRepo) {
        this.productColorRepo = productColorRepo;
    }

    // get all color list without pageble
    public List<ProductColor> findAll() {
        return productColorRepo.findAllOrderedByIdDesc();
    }

    public Page<ProductColor> getAllProductColors(Pageable pageable) {
        return productColorRepo.findAll(pageable);
    }

    public ProductColor getProductColorById(Long id) {
        Optional<ProductColor> productColor = productColorRepo.findById(id);
        return productColor.orElse(null);
    }

    @Transactional
    public ProductColor saveProductColor(ProductColor productColor) {
        return productColorRepo.save(productColor);
    }

    @Transactional
    public void deleteProductColorById(Long id) {
        productColorRepo.deleteById(id);
    }


}
