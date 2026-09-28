package com.petcare.branch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository
        extends JpaRepository<Branch, Long> {

    /*
     * Public booking
     */
    List<Branch>
    findByActiveTrueOrderByIdAsc();


    /*
     * Admin
     */
    List<Branch>
    findAllByOrderByIdAsc();
}
