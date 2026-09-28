package com.petcare.admin.pet;

import com.petcare.appointment.Appointment;
import com.petcare.appointment.AppointmentRepository;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;

import com.petcare.customer.Customer;
import com.petcare.customer.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPetService {

    private final PetRepository petRepository;

    private final CustomerRepository customerRepository;

    private final AppointmentRepository
            appointmentRepository;


    /*
     * =========================================================
     * SEARCH
     * =========================================================
     */

    @Transactional(readOnly = true)
    public List<Pet> search(
            String keyword
    ) {

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        return petRepository
                .searchAdminPets(
                        normalizedKeyword
                );
    }


    /*
     * =========================================================
     * DETAIL
     * =========================================================
     */

    @Transactional(readOnly = true)
    public Pet findById(
            Long id
    ) {

        return petRepository
                .findAdminDetailById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy thú cưng"
                        )
                );
    }


    /*
     * =========================================================
     * APPOINTMENT HISTORY
     * =========================================================
     */

    @Transactional(readOnly = true)
    public List<Appointment> findAppointments(
            Long petId
    ) {

        return appointmentRepository
                .findByPet_IdOrderByAppointmentDateDescStartTimeDesc(
                        petId
                );
    }


    /*
     * =========================================================
     * ENTITY -> FORM
     * =========================================================
     */

    @Transactional(readOnly = true)
    public AdminPetRequest toRequest(
            Pet pet
    ) {

        AdminPetRequest request =
                new AdminPetRequest();

        request.setCustomerId(
                pet.getCustomer() != null
                        ? pet.getCustomer().getId()
                        : null
        );

        request.setName(
                pet.getName()
        );

        request.setSpecies(
                pet.getSpecies()
        );

        request.setBreed(
                pet.getBreed()
        );

        request.setGender(
                pet.getGender()
        );

        request.setBirthDate(
                pet.getBirthDate()
        );

        return request;
    }


    /*
     * =========================================================
     * CREATE
     * =========================================================
     */

    @Transactional
    public Pet create(AdminPetRequest request) {

        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn chủ nuôi.");
        }

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chủ nuôi."));

        Pet pet = new Pet();
        pet.setCustomer(customer);
        pet.setName(request.getName().trim());
        pet.setSpecies(request.getSpecies().trim());
        pet.setBreed(normalizeNullable(request.getBreed()));
        pet.setGender(normalizeNullable(request.getGender()));
        pet.setBirthDate(request.getBirthDate());

        return petRepository.save(pet);
    }


    /*
     * =========================================================
     * UPDATE
     * =========================================================
     */

    @Transactional
    public Pet update(
            Long id,
            AdminPetRequest request
    ) {

        Pet pet =
                petRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Không tìm thấy thú cưng"
                                )
                        );

        pet.setName(
                request.getName().trim()
        );

        pet.setSpecies(
                request.getSpecies().trim()
        );

        pet.setBreed(
                normalizeNullable(
                        request.getBreed()
                )
        );

        pet.setGender(
                normalizeNullable(
                        request.getGender()
                )
        );

        pet.setBirthDate(
                request.getBirthDate()
        );

        return petRepository.save(pet);
    }


    /*
     * =========================================================
     * DELETE
     * =========================================================
     */

    @Transactional
    public void delete(Long id) {

        Pet pet = findById(id);

        if (appointmentRepository.existsByPet_Id(id)) {
            throw new IllegalArgumentException("Không thể xóa thú cưng đã có lịch hẹn trong hệ thống.");
        }

        petRepository.delete(pet);
    }


    /*
     * =========================================================
     * CUSTOMERS FOR SELECTION
     * =========================================================
     */

    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }


    private String normalizeNullable(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}
