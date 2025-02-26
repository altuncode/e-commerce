package com.altuncode.myshop.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(
            MaxUploadSizeExceededException exc,
            RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // Retrieve the Referer URL
        String referer = request.getHeader("Referer");

        // Add a validation error message
        redirectAttributes.addFlashAttribute("errorMessage", "The file size exceeds the allowed limit. The maximum total size allowed is " + maxRequestSize + ", and the maximum per file size is " + maxFileSize + ". Please reduce the file size or total request size accordingly.");
        return referer != null ? "redirect:" + referer : "redirect:/admin";
    }
}
