package com.altuncode.myshop.util.validator;

import com.altuncode.myshop.model.Photo;
import com.altuncode.myshop.model.interfaces.IProfuctFiles;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component("ProductFileValidator")
@Data
@NoArgsConstructor
public class ProductFileValidator implements Validator {

    private IProfuctFiles profuctFiles;
    @Value("${max-file}")
    private String maxFile;

    @Override
    public boolean supports(Class<?> clazz) {
        return IProfuctFiles.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        int maxFileCount = Integer.parseInt(maxFile);

        if (!(target instanceof List)) {
            errors.reject("invalid.target", "Target must be a list of MultipartFile objects.");
            return;
        }

        @SuppressWarnings("unchecked")
        List<MultipartFile> files = (List<MultipartFile>) target;

        if (files != null) {
            List<String> ALLOWED_EXTENSIONS = profuctFiles.getAllowedExtensions();
            List<String> ALLOWED_CONTENT_TYPES = profuctFiles.getAllowedContentTypes();
            String errorField = profuctFiles instanceof Photo ? "photos" : "productPdfs";
            String errorCode = profuctFiles instanceof Photo ? "error.photos" : "error.productPdfs";
            if(files.size() > maxFileCount){
                errors.rejectValue(errorField, errorCode, "You can upload a maximum of " + maxFile + " files.");
                return;
            }
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String extension = getFileExtension(file.getOriginalFilename());
                    String contentType = file.getContentType();

                    if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase()) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                        errors.rejectValue(errorField, errorCode, "Invalid file type for file: " + file.getOriginalFilename() + ". Only " + ALLOWED_EXTENSIONS.toString() + " are allowed.");
                        return;
                    }

                }
            }
        }

    }

    public String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }



}
