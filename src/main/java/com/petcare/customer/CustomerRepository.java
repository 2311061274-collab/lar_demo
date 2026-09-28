package com.petcare.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    /*
     * =========================================
     * PUBLIC BOOKING
     * =========================================
     */

    Optional<Customer> findFirstByPhone(
            String phone
    );


    /*
     * =========================================
     * ADMIN
     * =========================================
     */

    @Query("""
            SELECT c
            FROM Customer c

            WHERE
                :keyword = ''

                OR LOWER(c.fullName)
                    LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                OR c.phone
                    LIKE CONCAT('%', :keyword, '%')

                OR LOWER(
                    COALESCE(c.email, '')
                )
                    LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

            ORDER BY c.id DESC
            """)
    List<Customer> searchAdminCustomers(
            @Param("keyword")
            String keyword
    );
}
