package com.petcare.pet;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository
        extends JpaRepository<Pet, Long> {

    /*
     * =========================================================
     * PUBLIC BOOKING
     * =========================================================
     */

    Optional<Pet> findFirstByCustomerIdAndNameIgnoreCase(
            Long customerId,
            String name
    );


    /*
     * =========================================================
     * ADMIN CUSTOMER DETAIL
     * =========================================================
     */

    List<Pet> findByCustomer_IdOrderByIdAsc(
            Long customerId
    );


    /*
     * =========================================================
     * ADMIN PET SEARCH
     * =========================================================
     */

    @Query("""
            SELECT p
            FROM Pet p

            JOIN FETCH p.customer c

            WHERE
                :keyword = ''

                OR LOWER(p.name)
                    LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                OR LOWER(p.species)
                    LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                OR LOWER(c.fullName)
                    LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                OR c.phone
                    LIKE CONCAT('%', :keyword, '%')

            ORDER BY p.id DESC
            """)
    List<Pet> searchAdminPets(
            @Param("keyword")
            String keyword
    );


    /*
     * =========================================================
     * ADMIN PET DETAIL
     * =========================================================
     */

    @EntityGraph(
            attributePaths = {
                    "customer"
            }
    )
    @Query("""
            SELECT p
            FROM Pet p
            WHERE p.id = :id
            """)
    Optional<Pet> findAdminDetailById(
            @Param("id")
            Long id
    );
}
