package com.mezarlink.media;

import com.mezarlink.memorial.Memorial;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
public class Media {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "memorial_id", nullable = false)
    private Memorial memorial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    // R2/S3 uzerindeki tam URL (public-base-url + object key)
    @Column(nullable = false)
    private String url;

    @Column(name = "sort_order")
    private int sortOrder;

    public Media(Memorial memorial, MediaType type, String url, int sortOrder) {
        this.memorial = memorial;
        this.type = type;
        this.url = url;
        this.sortOrder = sortOrder;
    }
}
