package com.altuncode.myshop.repositories;

import com.altuncode.myshop.model.MyServicePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MyServicePhotoRepo")
public interface MyServicePhotoRepo extends JpaRepository<MyServicePhoto, Long> {
}
