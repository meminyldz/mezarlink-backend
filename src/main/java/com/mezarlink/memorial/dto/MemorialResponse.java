package com.mezarlink.memorial.dto;

import com.mezarlink.media.Media;
import com.mezarlink.media.MediaType;
import com.mezarlink.memorial.Memorial;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// Bu DTO'nun alan adlari bilerek React tarafindaki component prop'lariyla
// (MemorialHero, Gallery, VideoSection) ayni: frontend hicbir donusum
// yapmadan dogrudan kullanabilsin diye.
public record MemorialResponse(
        UUID id,
        String slug,
        String fullName,
        LocalDate birthDate,
        LocalDate deathDate,
        String biography,
        String coverPhoto,
        String profilePhoto,
        List<String> photos,
        List<String> videos
) {

    public static MemorialResponse from(Memorial memorial) {
        List<String> photos = memorial.getMedia().stream()
                .filter(m -> m.getType() == MediaType.PHOTO)
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(Media::getUrl)
                .toList();

        List<String> videos = memorial.getMedia().stream()
                .filter(m -> m.getType() == MediaType.VIDEO)
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(Media::getUrl)
                .toList();

        return new MemorialResponse(
                memorial.getId(),
                memorial.getSlug(),
                memorial.getFullName(),
                memorial.getBirthDate(),
                memorial.getDeathDate(),
                memorial.getBiography(),
                memorial.getCoverPhotoUrl(),
                memorial.getProfilePhotoUrl(),
                photos,
                videos
        );
    }
}
