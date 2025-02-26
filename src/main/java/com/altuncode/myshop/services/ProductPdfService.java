package com.altuncode.myshop.services;

import com.altuncode.myshop.model.ProductPdf;
import com.altuncode.myshop.repositories.ProductPdfRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("ProductPdfService")
public class ProductPdfService {

    private final ProductPdfRepo productPdfRepo;

    @Value("${file.files-dir}")
    private String uploadDir;



    @Autowired
    public ProductPdfService(@Qualifier("ProductPdfRepo") ProductPdfRepo productFileRepo) {
        this.productPdfRepo = productFileRepo;
    }


    @Transactional
    public ProductPdf savePdf(MultipartFile file) throws IOException {
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
        ProductPdf productPdf = new ProductPdf();
        productPdf.setUrl("/" + "files" + "/" + year + "/" + month + "/" + filename);
        return productPdfRepo.save(productPdf);
    }

    // Delete file by ID
    @Transactional
    public void deletePdf(Long id) throws IOException {
        Optional<ProductPdf> optionalProductPdf = productPdfRepo.findById(id);
        if (optionalProductPdf.isPresent()) {
            ProductPdf productPdf = optionalProductPdf.get();
            // Delete file from disk
            String pdfUrl = productPdf.getUrl(); // e.g., /uploads/2024/09/filename.jpg
            Path pdfPath = Paths.get(uploadDir).resolve(pdfUrl.replace("/files/", ""));
            Files.deleteIfExists(pdfPath);
            // Delete from DB
            productPdfRepo.delete(productPdf);
        }
    }

    //this method for update
    @Transactional
    public void updatePdf(Long id, String altText, Integer orderImg) {
        Optional<ProductPdf> optionalProductPdf = productPdfRepo.findById(id);
        if(optionalProductPdf.isPresent()){
            optionalProductPdf.get().setAltText(altText);
            optionalProductPdf.get().setOrderPdf(orderImg);
            productPdfRepo.save(optionalProductPdf.get());
        }
    }

    //this method genereate name for file
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

    public String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }


}
