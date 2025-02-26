package com.altuncode.myshop.repositories.projection;

import com.altuncode.myshop.model.enums.ProductStatusEnum;

import java.time.LocalDateTime;

public interface ProductProjectionUser {
    Long getId();
    String getName();
    String getUrl();
    String getSize();
    String getColor();
    Double getPrice();
    Double getNewPrice();
    Double getInstallPrice();
    String getFirstPhotoUrl();
    String getSecondPhotoUrl();
    ProductStatusEnum getProductStatusEnum();
    LocalDateTime getCreatedDate();
}
