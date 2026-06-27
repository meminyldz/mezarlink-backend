package com.mezarlink.memorial.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MemorialRequest(

        @NotBlank(message = "Ad soyad bos olamaz")
        String fullName,

        LocalDate birthDate,

        LocalDate deathDate,

        String biography
) {
}
