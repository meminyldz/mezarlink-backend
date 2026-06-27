package com.mezarlink.auth.dto;

import com.mezarlink.auth.User;

import java.util.UUID;

// Sifre hash'i asla disariya cikmaz, sadece bu alanlar frontend'e gider.
public record UserResponse(UUID id, String email, String fullName) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName());
    }
}
