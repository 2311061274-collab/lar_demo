package com.petcare.admin.customer;

import com.petcare.appointment.Appointment;
import com.petcare.appointment.AppointmentRepository;
import com.petcare.customer.Customer;
import com.petcare.customer.CustomerRepository;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCustomerService {

    private final CustomerRepository customerRepository;

    private final PetRepository petRepository;

    private final AppointmentRepository appointmentRepository;


    /*
     * =========================================
     * SEARCH CUSTOMER
     * =========================================
     */

    @Transactional(readOnly = true)
    public List<Customer> search(
            String keyword
    ) {

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        return customerRepository
                .searchAdminCustomers(
                        normalizedKeyword
                );
    }


    /*
     * =========================================
     * CUSTOMER DETAIL
     * =========================================
     */

    @Transactional(readOnly = true)
    public Customer findById(
            Long id
    ) {

        return customerRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy khách hàng"
                        )
                );
    }


    /*
     * =========================================
     * CUSTOMER PETS
     * =========================================
     */

    @Transactional(readOnly = true)
    public List<Pet> findPets(
            Long customerId
    ) {

        return petRepository
                .findByCustomer_IdOrderByIdAsc(
                        customerId
                );
    }


    /*
     * =========================================
     * APPOINTMENT HISTORY
     * =========================================
     */

    @Transactional(readOnly = true)
    public List<Appointment> findAppointments(
            Long customerId
    ) {

        return appointmentRepository
                .findByCustomer_IdOrderByAppointmentDateDescStartTimeDesc(
                        customerId
                );
    }
}
