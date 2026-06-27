package com.mezarlink.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Email bos olamaz")
        @Email(message = "Gecerli bir email adresi girin")
        String email,

        @NotBlank(message = "Sifre bos olamaz")
        String password
) {
}
