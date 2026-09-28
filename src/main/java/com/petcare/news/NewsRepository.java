package com.petcare.news;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NewsRepository
        extends JpaRepository<News, Long> {

    List<News>
    findByPublishedTrueOrderByPublishedAtDesc();


    @Query("""
            SELECT n FROM News n
            WHERE n.published = true
            AND (
                :keyword = ''
                OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(n.summary, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(n.content, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            ORDER BY n.publishedAt DESC
            """)
    List<News> searchPublishedNews(@Param("keyword") String keyword);


    Optional<News>
    findBySlugAndPublishedTrue(
            String slug
    );


    List<News>
    findAllByOrderByCreatedAtDesc();


    boolean existsBySlug(
            String slug
    );


    boolean existsBySlugAndIdNot(
            String slug,
            Long id
    );
}
