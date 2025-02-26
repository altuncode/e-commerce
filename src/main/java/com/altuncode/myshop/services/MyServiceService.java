package com.altuncode.myshop.services;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.repositories.MyServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service("MyServiceService")
@Transactional(readOnly = true)
public class MyServiceService {

    private final MyServiceRepo myServiceRepo;
    private final PhotoService photoService;

    @Autowired
    public MyServiceService(@Qualifier("MyServiceRepo") MyServiceRepo myServiceRepo, @Qualifier("PhotoService") PhotoService photoService) {
        this.myServiceRepo = myServiceRepo;
        this.photoService = photoService;
    }

    //*******************MyService*********************************//

    // List all MyService with pageable
    public Page<MyService> getAllMyServiceWithPage(Pageable pageable) {
        return myServiceRepo.findAll(pageable);
    }

    // Retrieve product by ID
    public MyService getMyServiceById(Long id) {
        return myServiceRepo.findById(id).orElse(null);
    }


    //Save a product
    @Transactional
    public MyService saveMyService(MyService myService) {
        MyService savedMyService = myServiceRepo.save(myService);
        return myServiceRepo.save(savedMyService);
    }

    //update existing product without save just send to saveProduct method
    @Transactional
    public void updateMyService(MyService existingMyService, MyService newMyService) {
        existingMyService.setName(newMyService.getName());
        existingMyService.setShortDescription(newMyService.getShortDescription());
        existingMyService.setActive(newMyService.getActive());
        existingMyService.setCreatedDate(newMyService.getCreatedDate());
        saveMyService(existingMyService);
    }

    //delete product by id
    @Transactional
    public void deleteMyServiceById(Long id) throws IOException {
        MyService myService = getMyServiceById(id);
        if(myService == null) {
            return;
        }
        if(myService.getPhotos() != null && myService.getPhotos().size() > 0) {
            for (MyServicePhoto photo : myService.getPhotos()) {
                photoService.deletePhotoForMyservice(photo.getId());
            }
        }
        myServiceRepo.deleteById(id);
    }

    private String generateUrl(MyService myService) {
        String orginalName = myService.getName();
        String newName = orginalName.trim().toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-").replace("--","-");
        return newName + "-" + myService.getId();
    }
}
