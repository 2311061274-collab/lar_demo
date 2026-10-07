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
            LocalTime requestedTime
    ) {
        if (requestedTime.isBefore(LocalTime.of(8, 0)) || requestedTime.isAfter(LocalTime.of(21, 0))) {
            throw new IllegalArgumentException(
                    "Phòng khám nhận lịch hẹn khám thường từ 08:00 đến 21:00 hàng ngày (sau 21:00 xin vui lòng liên hệ trực tiếp hotline Cấp cứu 24/7)."
            );
        }

        List<AppointmentStatus> blockingStatuses =
                List.of(
                        AppointmentStatus.PENDING,
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.IN_PROGRESS
                );

        List<Appointment> existingAppointments =
                appointmentRepository.findByBranch_IdAndAppointmentDateAndStatusIn(
                        branchId,
                        date,
                        blockingStatuses
                );

        for (Appointment existing : existingAppointments) {
            long minutesBetween = Math.abs(java.time.Duration.between(existing.getStartTime(), requestedTime).toMinutes());
            if (minutesBetween < 30) {
                throw new IllegalArgumentException(
                        String.format("Khung giờ %s ngày %s đã có lịch hẹn (lúc %s). Mỗi ca khám cách nhau tối thiểu 30 phút để bác sĩ chuẩn bị chu đáo nhất. Quý khách vui lòng chọn giờ khác!",
                                requestedTime.toString(),
                                date.toString(),
                                existing.getStartTime().toString())
                );
            }
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
