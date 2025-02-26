package com.altuncode.myshop.services;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.repositories.*;
import com.altuncode.myshop.repositories.projection.ProductProjectionUser;
import com.altuncode.myshop.repositories.projection.ProductSetsProjectionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service("MainUserService")
@Transactional(readOnly = true)
public class MainUserService {

    //*************Varaibles***************//

    private final ProductCategoryRepo productCategoryRepo;
    private final ProductSubCategoryRepo productSubCategoryRepo;
    private final ProductRepo productRepoUser;
    private final ProductSetsRepo productSetsRepo;
    private final MyServiceRepo myServiceRepo;
    private final ProductSetsPhotoRepo productSetsPhotoRepo;
    private final ProductPhotoRepo productPhotoRepo;



    //*************Constructor***************//

    @Autowired
    public MainUserService(@Qualifier("ProductCategoryRepo") ProductCategoryRepo productCategoryRepo, @Qualifier("ProductSubCategoryRepo") ProductSubCategoryRepo productSubCategoryRepo, @Qualifier("ProductRepo") ProductRepo productRepoUser, @Qualifier("ProductSetsRepo") ProductSetsRepo productSetsRepo, @Qualifier("MyServiceRepo") MyServiceRepo myServiceRepo, @Qualifier("ProductSetsPhotoRepo") ProductSetsPhotoRepo productSetsPhotoRepo, @Qualifier("ProductPhotoRepo") ProductPhotoRepo productPhotoRepo) {
        this.productCategoryRepo = productCategoryRepo;
        this.productSubCategoryRepo = productSubCategoryRepo;
        this.productRepoUser = productRepoUser;
        this.productSetsRepo = productSetsRepo;
        this.myServiceRepo = myServiceRepo;
        this.productSetsPhotoRepo = productSetsPhotoRepo;
        this.productPhotoRepo = productPhotoRepo;
    }

    //*************Categories***************//

    //bitdi 100%
    //get all list of active categories by ordered orderNumber asc
    public List<ProductCategory> getAllActiveProductCategoryOrderByOrderNumberAsc() {
        return productCategoryRepo.findByActiveTrueOrderByOrderNumberAsc();
    }

    //get one category by id
    public ProductCategory getCategoryById(Long id) {
        return productCategoryRepo.findById(id).orElse(null);
    }

    //*************Subcategory***************//

    //get all list of active subcategories by ordered orderNumber asc
    public List<ProductSubCategory> getSubCategoriesForCategory(ProductCategory category) {
        return productSubCategoryRepo.findByProductCategoryAndActiveTrueOrderByOrderNumberAsc(category);
    }

    //get one subcategory by id
    public ProductSubCategory getSubCategoryById(Long id) {
        return productSubCategoryRepo.findById(id).orElse(null);
    }

    //*************Products***************//

    //get one product by name
    public Product getOneProduct(String name) {
        return productRepoUser.findByUrlAndActiveTrue(name);
    }

    //get all product related with categoryID by ordered created asc
    public Page<ProductProjectionUser> getAllProductListWithPage(Pageable pageable) {
        return productRepoUser.findAllActiveProducts(pageable);
    }

    //get all product related with categoryID by ordered created asc
    public Page<ProductProjectionUser> getAllProductListByCategoryUrlWithPage(String categoryUrl, Pageable pageable) {
        return productRepoUser.findActiveProductsByCategoryUrl(categoryUrl,pageable);
    }

    //get all product related with subcategoryId by ordered created asc
    public Page<ProductProjectionUser> getAllProductListBySubCategoryUrlWithPage(String categoryUrl, String subCategoryUrl, Pageable pageable) {
        return productRepoUser.findActiveProductsByCategoryUrlAndSubCategoryUrl(categoryUrl, subCategoryUrl, pageable);
    }

    //get all product related with categoryID by ordered created asc
    public Page<ProductProjectionUser> searchAllProductListWithPage(String name, Pageable pageable) {
        return productRepoUser.searchAllActiveProducts(name, pageable);
    }

    //*************ProductSets****************//

    public ProductSets getProductSetsById(Long id) {
        return productSetsRepo.findById(id).orElse(null);
    }

    public ProductSets getProductSetsByUrl(String name) {
        return productSetsRepo.findByUrlAndActiveTrue(name);
    }

    //get all product related with categoryID by ordered created asc
    public Page<ProductSetsProjectionUser> getAllProductSetsListWithPage(Pageable pageable) {
        return productSetsRepo.findAllActiveProductSets(pageable);
    }

    //*************MySeruve****************//

    //get all product related with categoryID by ordered created asc
    public List<MyService> getAllMySerive() {
        return myServiceRepo.findAllActiveMyService();
    }

    //*************Photos****************//

    //get all product related with categoryID by ordered created asc
    public List<ProductPhoto> getAllPhotosByProductId(Long productId) {
        return productPhotoRepo.getProductPhotoByProductId(productId);
    }

    public List<ProductSetsPhoto> getAllPhotosByProducSetstId(Long productSetsId) {
        return productSetsPhotoRepo.getProductSetsPhotoByProductSetsId(productSetsId);
    }


}
