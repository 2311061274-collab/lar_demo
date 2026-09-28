package com.petcare.site;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "site_settings")
@Getter
@Setter
@NoArgsConstructor
public class SiteSetting {

    @Id
    private Long id;

    @Column(
            name = "clinic_name",
            nullable = false
    )
    private String clinicName;

    @Column(length = 500)
    private String tagline;

    @Column(name = "about_title")
    private String aboutTitle;

    @Column(
            name = "about_content",
            columnDefinition = "TEXT"
    )
    private String aboutContent;

    @Column(length = 500)
    private String address;

    @Column(length = 50)
    private String phone;

    private String email;

    @Column(
            name = "opening_hours",
            length = 500
    )
    private String openingHours;

    @Column(
            name = "facebook_url",
            length = 500
    )
    private String facebookUrl;

    @Column(
            name = "map_url",
            length = 1000
    )
    private String mapUrl;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
