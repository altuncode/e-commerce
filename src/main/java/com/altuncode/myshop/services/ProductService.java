package com.altuncode.myshop.services;

import com.altuncode.myshop.model.Product;
import com.altuncode.myshop.model.ProductPdf;
import com.altuncode.myshop.model.ProductPhoto;
import com.altuncode.myshop.model.ProductSets;
import com.altuncode.myshop.repositories.ProductRepo;
import com.altuncode.myshop.repositories.ProductSetsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service("ProductService")
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductPdfService productPdfService;
    private final PhotoService photoService;
    private final ProductSetsRepo productSetsRepo;

    @Autowired
    public ProductService(@Qualifier("ProductRepo") ProductRepo productRepo, @Qualifier("ProductPdfService") ProductPdfService productPdfService, @Qualifier("PhotoService") PhotoService photoService, @Qualifier("ProductSetsRepo") ProductSetsRepo productSetsRepo) {
        this.productRepo = productRepo;
        this.productPdfService = productPdfService;
        this.photoService = photoService;
        this.productSetsRepo = productSetsRepo;
    }

    // List all products with pageable
    public Page<Product> getAllProductList(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    // List all products without pageable
    public List<Product> getAllActiveProductList() {
        return productRepo.findByActiveTrue(Sort.by(Sort.Direction.DESC, "id"));
    }

    // Retrieve product by ID
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    @Transactional
    public void linkRelatedProducts(Product product, List<Long> relatedProductIds) {
        // get all related products
        List<Product> newProductList = null;

        // Check if all related products are found
        if(relatedProductIds != null && relatedProductIds.size() > 0) {
            newProductList = productRepo.findAllById(relatedProductIds);
        }

        // Clear existing associations
        List<Product> oldRelatedProducts = product.getRelatedProducts();
        List<Product> productsToRemove = new ArrayList<>(oldRelatedProducts); // Temporary list

        // Remove associations in a separate loop
        for (Product associatedProduct : productsToRemove) {
            associatedProduct.removeAssociatedProduct(product); // Remove inverse relationship
            productRepo.save(associatedProduct); // Save the associated product
        }
        oldRelatedProducts.clear(); // Clear the collection safely

        // Link with new associated books
        if (newProductList != null) {
            for (Product newProduct : newProductList) {
                newProduct.addAssociatedProduct(product);
                productRepo.save(newProduct);
            }
        }
    }

    public List<Product> getProductListByIds(List<Long> relatedProductIds){
        // Check if all related products are found
        if(relatedProductIds != null && relatedProductIds.size() > 0) {
            return productRepo.findAllById(relatedProductIds);
        }
        return null;
    }

    //Save a product
    @Transactional
    public Product saveProduct(Product product) {
        Product savedProduct = productRepo.save(product);
        savedProduct.setUrl(generateUrl(savedProduct));
        // Recalculate associated ProductSets
        if (product.getProductSets() != null && !product.getProductSets().isEmpty()) {
            for (ProductSets productSet : product.getProductSets()) {
                productSet.updatePrices();
                productSetsRepo.save(productSet);
            }
        }
        return productRepo.save(savedProduct);
    }

    //update existing product without save just send to saveProduct method
    @Transactional
    public void updateProduct(Product existingProduct, Product newProduct) {
        existingProduct.setName(newProduct.getName());
        existingProduct.getDescriptionProduct().setDetail(newProduct.getDescriptionProduct().getDetail());
        existingProduct.setShortDescription(newProduct.getShortDescription());
        existingProduct.setQuantity(newProduct.getQuantity());
        existingProduct.setPrice(newProduct.getPrice());
        existingProduct.setNewPrice(newProduct.getNewPrice());
        existingProduct.setInstallPrice(newProduct.getInstallPrice());
        existingProduct.setWidth(newProduct.getWidth());
        existingProduct.setHeight(newProduct.getHeight());
        existingProduct.setDepth(newProduct.getDepth());
        existingProduct.setActive(newProduct.getActive());
        existingProduct.setProductColor(newProduct.getProductColor());
        existingProduct.setProductStatusEnum(newProduct.getProductStatusEnum());
        existingProduct.setCreatedDate(newProduct.getCreatedDate());
        existingProduct.setProductSubCategoryList(newProduct.getProductSubCategoryList());
        saveProduct(existingProduct);
    }

    private String generateUrl(Product product) {
        String orginalName = product.getName();
        String newName = orginalName.trim().toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-").replace("--","-");
        return newName + "-" + product.getId();
    }

    //delete product by id
    @Transactional
    public void deleteProductById(Long id) throws IOException {
        Product product = getProductById(id);
        if(product == null) {
            return;
        }

        // Handle deleted pdf
        if (product.getProductPdfs() != null && product.getProductPdfs().size() > 0) {
            for (ProductPdf productPdf : product.getProductPdfs()) {
                productPdfService.deletePdf(productPdf.getId());
            }
        }

        if(product.getPhotos() != null && product.getPhotos().size() > 0) {
            for (ProductPhoto photo : product.getPhotos()) {
                photoService.deletePhotoForProduct(photo.getId());
            }
        }
        linkRelatedProducts(product, null);
        productRepo.deleteById(id);
    }

}
