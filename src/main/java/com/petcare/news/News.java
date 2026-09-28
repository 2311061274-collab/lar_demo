package com.petcare.news;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;


    @Column(
            nullable = false,
            unique = true
    )
    private String slug;


    @Column(length = 1000)
    private String summary;


    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;


    @Column(
            name = "image_url",
            length = 500
    )
    private String imageUrl;


    @Column(nullable = false)
    private Boolean published = false;


    @Column(name = "published_at")
    private LocalDateTime publishedAt;


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


    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (published == null) {
            published = false;
        }
    }


    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}
