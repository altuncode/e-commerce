package com.altuncode.myshop.services;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.repositories.MyServicePhotoRepo;
import com.altuncode.myshop.repositories.ProductPhotoRepo;
import com.altuncode.myshop.repositories.ProductSetsPhotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.file.*;

@Service("PhotoService")
public class PhotoService {

    private final ProductPhotoRepo productPhotoRepo;
    private final MyServicePhotoRepo myServicePhotoRepo;
//    private final BlogPhotoRepo blogPhotoRepo;
    private final ProductSetsPhotoRepo productSetsPhotoRepo;

    @Value("${file.images-dir}")
    private String uploadDir;

    // Allowed file types
    public static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    public static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");

    @Autowired
    public PhotoService(@Qualifier("ProductPhotoRepo") ProductPhotoRepo productPhotoRepo, @Qualifier("MyServicePhotoRepo") MyServicePhotoRepo myServicePhotoRepo, @Qualifier("ProductSetsPhotoRepo") ProductSetsPhotoRepo productSetsPhotoRepo) {
        this.productPhotoRepo = productPhotoRepo;
        this.myServicePhotoRepo = myServicePhotoRepo;
        this.productSetsPhotoRepo = productSetsPhotoRepo;
    }

    //////////////////////////////// ProductPhoto ////////////////////////////////

    // this method for save image
    @Transactional
    public ProductPhoto savePhotoForProduct(MultipartFile file) throws IOException {
        // Get current year and month
        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear());
        String month = String.format("%02d", currentDate.getMonthValue());

        // Generate filename
        String randomName = generateCustomString();
        String extension = getFileExtension(file.getOriginalFilename());
        String filename = randomName + "." + extension;

        // Define the path: uploads/year/month/
        Path uploadPath = Paths.get(uploadDir, year, month).toAbsolutePath().normalize();

        // Ensure directories exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to disk
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create Image entity with URL pointing to /uploads/{year}/{month}/filename.ext
        ProductPhoto photo = new ProductPhoto();
        photo.setUrl("/" + "files" + "/" + year + "/" + month + "/" + filename);
        return productPhotoRepo.save(photo);
    }

    // Delete image by ID
    @Transactional
    public void deletePhotoForProduct(Long id) throws IOException {
        Optional<ProductPhoto> photoOptional = productPhotoRepo.findById(id);
        if (photoOptional.isPresent()) {
            ProductPhoto productPhoto = photoOptional.get();
            // Delete file from disk
            String imageUrl = productPhoto.getUrl(); // e.g., /uploads/2024/09/filename.jpg
            Path filePath = Paths.get(uploadDir).resolve(imageUrl.replace("/files/", ""));
            Files.deleteIfExists(filePath);
            // Delete from DB
            productPhotoRepo.delete(productPhoto);
        }
    }

    //this method for update
    @Transactional
    public void updatePhotoDetailForProduct(Long id, Integer orderImg) {
        Optional<ProductPhoto> productPhoto = productPhotoRepo.findById(id);
        if(productPhoto.isPresent()){
            productPhoto.get().setOrderImg(orderImg);
            productPhotoRepo.save(productPhoto.get());
        }

    }


    //////////////////////////////// MyServicePhoto ////////////////////////////////
    // this method for save image
    @Transactional
    public MyServicePhoto savePhotoForMyservice(MultipartFile file) throws IOException {
        // Get current year and month
        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear());
        String month = String.format("%02d", currentDate.getMonthValue());

        // Generate filename
        String randomName = generateCustomString();
        String extension = getFileExtension(file.getOriginalFilename());
        String filename = randomName + "." + extension;

        // Define the path: uploads/year/month/
        Path uploadPath = Paths.get(uploadDir, year, month).toAbsolutePath().normalize();

        // Ensure directories exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to disk
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create Image entity with URL pointing to /uploads/{year}/{month}/filename.ext
        MyServicePhoto photo = new MyServicePhoto();
        photo.setUrl("/" + "files" + "/" + year + "/" + month + "/" + filename);
        return myServicePhotoRepo.save(photo);
    }

    // Delete image by ID
    @Transactional
    public void deletePhotoForMyservice(Long id) throws IOException {
        Optional<MyServicePhoto> photoOptional = myServicePhotoRepo.findById(id);
        if (photoOptional.isPresent()) {
            MyServicePhoto photo = photoOptional.get();
            // Delete file from disk
            String imageUrl = photo.getUrl(); // e.g., /uploads/2024/09/filename.jpg
            Path filePath = Paths.get(uploadDir).resolve(imageUrl.replace("/files/", ""));
            Files.deleteIfExists(filePath);
            // Delete from DB
            myServicePhotoRepo.delete(photo);
        }
    }

    //this method for update
    @Transactional
    public void updatePhotoDetailForMyservice(Long id, Integer orderImg) {
        Optional<MyServicePhoto> photo = myServicePhotoRepo.findById(id);
        if(photo.isPresent()){
            photo.get().setOrderImg(orderImg);
            myServicePhotoRepo.save(photo.get());
        }

    }

    //////////////////////////////// ProductSetsPhoto ////////////////////////////////
    // this method for save image
    @Transactional
    public ProductSetsPhoto savePhotoForProductSets(MultipartFile file) throws IOException {
        // Get current year and month
        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear());
        String month = String.format("%02d", currentDate.getMonthValue());

        // Generate filename
        String randomName = generateCustomString();
        String extension = getFileExtension(file.getOriginalFilename());
        String filename = randomName + "." + extension;

        // Define the path: uploads/year/month/
        Path uploadPath = Paths.get(uploadDir, year, month).toAbsolutePath().normalize();

        // Ensure directories exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to disk
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create Image entity with URL pointing to /uploads/{year}/{month}/filename.ext
        ProductSetsPhoto photo = new ProductSetsPhoto();
        photo.setUrl("/" + "files" + "/" + year + "/" + month + "/" + filename);
        return productSetsPhotoRepo.save(photo);
    }

    // Delete image by ID
    @Transactional
    public void deletePhotoForProductSets(Long id) throws IOException {
        Optional<ProductSetsPhoto> photoOptional = productSetsPhotoRepo.findById(id);
        if (photoOptional.isPresent()) {
            ProductSetsPhoto photo = photoOptional.get();
            // Delete file from disk
            String imageUrl = photo.getUrl(); // e.g., /uploads/2024/09/filename.jpg
            Path filePath = Paths.get(uploadDir).resolve(imageUrl.replace("/files/", ""));
            Files.deleteIfExists(filePath);
            // Delete from DB
            productSetsPhotoRepo.delete(photo);
        }
    }

    //this method for update
    @Transactional
    public void updatePhotoDetailForProductSets(Long id, Integer orderImg) {
        Optional<ProductSetsPhoto> photo = productSetsPhotoRepo.findById(id);
        if(photo.isPresent()){
            photo.get().setOrderImg(orderImg);
            productSetsPhotoRepo.save(photo.get());
        }

    }

    //////////////////////////////// Universal Methods ////////////////////////////////

    // This method only for create name
    public static String generateCustomString() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        //Random words
        String randomUUID = UUID.randomUUID().toString().replace("-", "").substring(0, 8);


        String date = now.format(dateFormatter);
        String time = now.format(timeFormatter);

        // Get timezone offset in hours
        ZoneOffset offset = now.getOffset();
        int totalSeconds = offset.getTotalSeconds();
        int hoursOffset = totalSeconds / 3600;


        String offsetStr;
        if (hoursOffset > 0) {
            offsetStr = "p" + hoursOffset;
        } else if (hoursOffset < 0) {
            offsetStr = "m" + Math.abs(hoursOffset);
        } else {
            offsetStr = "e";
        }

        // Get nanoseconds, padded to 9 digits
        int nano = now.getNano();
        String nanoStr = String.format("%09d", nano);

        // Generate a random 6-digit number, padded with leading zeros if necessary
        Random random = new Random();
        int randomNumber = random.nextInt(10000); // 0 to 9999
        String randomStr = String.format("%04d", randomNumber);

        // Combine all parts into the final string
        String result = date + time + "tz" + offsetStr + nanoStr + randomUUID +randomStr;

        return result;
    }

    // Helper method to extract file extension
    public String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

}
