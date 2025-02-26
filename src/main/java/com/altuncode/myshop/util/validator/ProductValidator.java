package com.altuncode.myshop.util.validator;

import com.altuncode.myshop.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("ProductValidator")
public class ProductValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    Product product = (Product) target;
    if (product.getNewPrice() !=null && product.getNewPrice()>0 && product.getNewPrice()>=product.getPrice()) {
        errors.rejectValue("newPrice", "", "The price of a discounted item cannot be higher than the base price.");
    }

    if (product.getProductColor() == null) {
        errors.rejectValue("productColor", "", "Color is required");
    }

    }
}
