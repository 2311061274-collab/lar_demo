package com.petcare.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VetServiceRepository
        extends JpaRepository<VetService, Long> {

    // Public website
    List<VetService> findByActiveTrueOrderByIdAsc();

    Optional<VetService> findBySlugAndActiveTrue(
            String slug
    );


    // Admin
    List<VetService> findAllByOrderByIdAsc();

    boolean existsBySlug(
            String slug
    );

    boolean existsBySlugAndIdNot(
            String slug,
            Long id
    );
}
