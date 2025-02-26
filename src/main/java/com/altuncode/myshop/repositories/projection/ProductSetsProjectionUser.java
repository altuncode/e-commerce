package com.altuncode.myshop.repositories.projection;

import java.time.LocalDateTime;

public interface ProductSetsProjectionUser {
    Long getId();
    String getName();
    String getUrl();
    Double getPrice();
    String getFirstPhotoUrl();
    String getSecondPhotoUrl();
    LocalDateTime getCreatedDate();
}
