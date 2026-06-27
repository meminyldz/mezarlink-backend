package com.mezarlink.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Ad soyad bos olamaz")
        String fullName,

        @NotBlank(message = "Email bos olamaz")
        @Email(message = "Gecerli bir email adresi girin")
        String email,

        @NotBlank(message = "Sifre bos olamaz")
        @Size(min = 8, message = "Sifre en az 8 karakter olmali")
        String password
) {
}
