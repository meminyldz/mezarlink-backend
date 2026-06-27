package com.mezarlink.memorial;

import com.mezarlink.auth.User;
import com.mezarlink.media.Media;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "memorials")
@Getter
@Setter
@NoArgsConstructor
public class Memorial {

    @Id
    @GeneratedValue
    private UUID id;

    // QR kodun yonlendirdigi adresteki benzersiz parca: /m/{slug}
    @Column(nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "cover_photo_url")
    private String coverPhotoUrl;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @OneToMany(mappedBy = "memorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> media = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
