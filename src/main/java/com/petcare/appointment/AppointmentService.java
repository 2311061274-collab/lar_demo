package com.petcare.appointment;

import com.petcare.branch.Branch;
import com.petcare.branch.BranchService;
import com.petcare.customer.Customer;
import com.petcare.customer.CustomerRepository;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import com.petcare.service.VetService;
import com.petcare.service.VetServiceService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;
    private final VetServiceService vetServiceService;
    private final BranchService branchService;


    @Transactional
    public Appointment createAppointment(
            AppointmentRequest request
    ) {

        validateAppointmentTime(request);

        VetService service =
                vetServiceService.getActiveById(
                        request.getServiceId()
                );

        Branch branch =
                branchService.getActiveById(
                        request.getBranchId()
                );

        validateSlot(
                branch.getId(),
                request.getAppointmentDate(),
                request.getStartTime()
        );

        Customer customer =
                findOrCreateCustomer(request);

        Pet pet =
                findOrCreatePet(
                        customer,
                        request
                );

        Appointment appointment =
                new Appointment();

        appointment.setCode(
                generateAppointmentCode()
        );

        appointment.setCustomer(customer);
        appointment.setPet(pet);
        appointment.setService(service);
        appointment.setBranch(branch);

        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );

        appointment.setStartTime(
                request.getStartTime()
        );

        appointment.setVisitType(
                request.getVisitType()
        );

        appointment.setVisitAddress(
                request.getVisitAddress()
        );

        appointment.setStatus(
                AppointmentStatus.PENDING
        );

        appointment.setNote(
                request.getNote()
        );

        return appointmentRepository.save(
                appointment
        );
    }


    private Customer findOrCreateCustomer(
            AppointmentRequest request
    ) {

        Customer customer =
                customerRepository
                        .findFirstByPhone(
                                request.getPhone()
                        )
                        .orElseGet(Customer::new);

        customer.setFullName(
                request.getCustomerName()
        );

        customer.setPhone(
                request.getPhone()
        );

        customer.setEmail(
                request.getEmail()
        );

        customer.setAddress(
                request.getCustomerAddress()
        );

        return customerRepository.save(
                customer
        );
    }


    private Pet findOrCreatePet(
            Customer customer,
            AppointmentRequest request
    ) {

        Pet pet =
                petRepository
                        .findFirstByCustomerIdAndNameIgnoreCase(
                                customer.getId(),
                                request.getPetName()
                        )
                        .orElseGet(Pet::new);

        pet.setCustomer(customer);

        pet.setName(
                request.getPetName()
        );

        pet.setSpecies(
                request.getSpecies()
        );

        pet.setBreed(
                request.getBreed()
        );

        pet.setGender(
                request.getGender()
        );

        return petRepository.save(
                pet
        );
    }


    private void validateSlot(
            Long branchId,
            LocalDate date,
            LocalTime time
    ) {

        List<AppointmentStatus> blockingStatuses =
                List.of(
                        AppointmentStatus.PENDING,
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.IN_PROGRESS
                );

        boolean occupied =
                appointmentRepository
                        .existsByBranch_IdAndAppointmentDateAndStartTimeAndStatusIn(
                                branchId,
                                date,
                                time,
                                blockingStatuses
                        );

        if (occupied) {
            throw new IllegalArgumentException(
                    "Khung giờ này đã có lịch đặt. "
                            + "Vui lòng chọn giờ khác."
            );
        }
    }


    private void validateAppointmentTime(
            AppointmentRequest request
    ) {

        LocalDate today =
                LocalDate.now();

        if (request.getAppointmentDate() == null
                || request.getStartTime() == null) {
            return;
        }

        if (request.getAppointmentDate()
                .isBefore(today)) {

            throw new IllegalArgumentException(
                    "Không thể đặt lịch trong quá khứ."
            );
        }

        if (request.getAppointmentDate()
                .equals(today)
                && request.getStartTime()
                .isBefore(LocalTime.now())) {

            throw new IllegalArgumentException(
                    "Không thể chọn giờ đã qua."
            );
        }

        if (request.getVisitType()
                == VisitType.AT_HOME) {

            if (request.getVisitAddress() == null
                    || request.getVisitAddress()
                    .isBlank()) {

                throw new IllegalArgumentException(
                        "Vui lòng nhập địa chỉ "
                                + "khi chọn bác sĩ đến nhà."
                );
            }
        }
    }


    private String generateAppointmentCode() {

        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 6)
                        .toUpperCase();

        String date =
                LocalDate.now()
                        .toString()
                        .replace("-", "");

        return "APT-" + date + "-" + random;
    }
}
