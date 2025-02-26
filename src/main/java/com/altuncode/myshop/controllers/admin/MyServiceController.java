package com.altuncode.myshop.controllers.admin;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.services.MyServiceService;
import com.altuncode.myshop.services.PhotoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.altuncode.myshop.util.validator.ProductFileValidator;

import java.io.IOException;
import java.util.List;


@Controller("MyServiceController")
@RequestMapping("/admin/product/myservice")
@PreAuthorize("hasRole('ADMIN')")
public class MyServiceController {

    private final MyServiceService myServiceService;
    private final PhotoService photoService;
    private final ProductFileValidator productFileValidator;

    @Autowired
    public MyServiceController(@Qualifier("MyServiceService") MyServiceService myServiceService, @Qualifier("PhotoService") PhotoService photoService, @Qualifier("ProductFileValidator") ProductFileValidator productFileValidator) {
        this.myServiceService = myServiceService;
        this.photoService = photoService;
        this.productFileValidator = productFileValidator;
    }

    // Get all products
    @GetMapping({"", "/"})
    public String allGET(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<MyService> myServices = myServiceService.getAllMyServiceWithPage(PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("myServices", myServices);
        model.addAttribute("totalItems", myServices.getTotalElements());
        model.addAttribute("totalPage", myServices.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", 10);
        return "admin/product/myservice/list";
    }

    // Add new product
    @GetMapping({"add", "add/"})
    public String addGET(Model model) {
        model.addAttribute("mySerive", new MyService());
        return "admin/product/myservice/addedit";
    }

    //Save new product
    @PostMapping({"add","add/"})
    public String addPOST(@ModelAttribute("mySerive") @Valid MyService mySerive, BindingResult bindingResult, @RequestParam(value = "imagess", required = false) List<MultipartFile> images, Model model) {
        //check validation
        productFileValidator.setProfuctFiles(new Photo());
        productFileValidator.validate(images, bindingResult);


        if(bindingResult.hasErrors()) {
            return "admin/product/myservice/addedit";
        }
        try {
            // Save product first to generate ID
            MyService savedMyservice = myServiceService.saveMyService(mySerive);

            // Save each image and associate with the book
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        MyServicePhoto image = photoService.savePhotoForMyservice(file);
                        savedMyservice.addPhoto(image);
                    }
                }
            }
            // Save book again with images
            myServiceService.saveMyService(savedMyservice);
            return "redirect:/admin/product/myservice";
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.rejectValue("images", "error.images",
                    "An error occurred while uploading files.");
            return "redirect:/admin/product/myservice";
        }
    }

    // Update existing product
    @PatchMapping({"edit/{id}", "edit/{id}/"})
    public String updatePOST(@PathVariable("id") Long id,
                             @ModelAttribute("mySerive") @Valid MyService mySerive,
                             BindingResult bindingResult,
                             @RequestParam(value = "imagess", required = false) List<MultipartFile> images,
                             @RequestParam(value = "deletedImg", required = false) List<Long> deletedImgIds,
                             Model model) {
        //check validation
        productFileValidator.setProfuctFiles(new Photo());
        productFileValidator.validate(images, bindingResult);


        if (bindingResult.hasErrors()) {
            return "admin/product/myservice/addedit";
        }

        try {
            // Fetch existing product from the database
            MyService existingMyService = myServiceService.getMyServiceById(id);

            if (existingMyService == null) {
                return "redirect:/admin/product/myservice"; // Redirect if product not found
            }

            //hange update img
            if(!mySerive.getPhotos().isEmpty()){
                for(MyServicePhoto photo: mySerive.getPhotos()){
                    photoService.updatePhotoDetailForMyservice(photo.getId(),photo.getOrderImg());
                }
            }

            // Handle deleted images
            if (deletedImgIds != null) {
                for (Long imgId : deletedImgIds) {
                    photoService.deletePhotoForMyservice(imgId);
                }
            }
            // Handle new images
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        MyServicePhoto image = photoService.savePhotoForMyservice(file);
                        existingMyService.addPhoto(image);
                    }
                }
            }
            // Save updated product
            myServiceService.updateMyService(existingMyService, mySerive);
            return "redirect:/admin/product/myservice";
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.rejectValue("images", "error.images",
                    "An error occurred while uploading files.");
            return "admin/product/myservice/addedit";
        }
    }

    // Update product - Display the edit form
    @GetMapping({"edit/{id}", "edit/{id}/"})
    public String editGET(@PathVariable("id") Long id, Model model) {
        // Fetch the product by ID
        MyService myService = myServiceService.getMyServiceById(id);
        if (myService == null) {
            return "redirect:/admin/product/myservice"; // Redirect if product not found
        }
        // Populate model attributes
        model.addAttribute("mySerive", myService);
        return "admin/product/myservice/addedit";
    }


    // Altun buna baxdi
    // Delete color
    @DeleteMapping({"delete/{id}","delete/{id}/"})
    public String deleteDELETE(@PathVariable("id") Long id) throws IOException {
        if (myServiceService.getMyServiceById(id) == null) {
            return "redirect:/admin/product/myservice";
        }
        myServiceService.deleteMyServiceById(id);
        return "redirect:/admin/product/myservice";
    }
}
