package com.altuncode.myshop.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdressDTO {

    @NotBlank(message = "Field cannot be empty or contain only spaces.")
    @Size(min = 2, max = 100, message = "Field must be between 2 and 100 characters.")
    private String fullName;

    @NotBlank(message = "Field cannot be empty or contain only spaces.")
    @Size(min = 2, max = 100, message = "Field must be between 2 and 100 characters.")
    private String phoneNumber;

    @NotBlank(message = "Field cannot be empty or contain only spaces.")
    @Size(min = 2, max = 100, message = "Field must be between 2 and 100 characters.")
    private String address;

    @NotBlank(message = "Field cannot be empty or contain only spaces.")
    @Size(min = 2, max = 100, message = "Field must be between 2 and 100 characters.")
    private String address2;

    private String companyName;

    private String city;

    @NotBlank(message = "Field cannot be empty or contain only spaces.")
    @Size(min = 2, max = 100, message = "Field must be between 2 and 100 characters.")
    private String postalCode;

    private String note;

    @Column(name = "active")
    private Boolean active = false;
}
