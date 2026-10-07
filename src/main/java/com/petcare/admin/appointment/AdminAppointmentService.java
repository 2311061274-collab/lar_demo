package com.petcare.admin.appointment;

import com.petcare.appointment.Appointment;
import com.petcare.appointment.AppointmentRepository;
import com.petcare.appointment.AppointmentStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAppointmentService {

    private final AppointmentRepository appointmentRepository;


    @Transactional(readOnly = true)
    public List<Appointment> search(
            String keyword,
            AppointmentStatus status,
            LocalDate appointmentDate
    ) {

        return appointmentRepository
                .searchAdminAppointments(
                        keyword,
                        status,
                        appointmentDate
                );
    }


    @Transactional(readOnly = true)
    public Appointment getById(Long id) {

        return appointmentRepository
                .findDetailById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy lịch hẹn"
                        )
                );
    }


    @Transactional
    public Appointment confirm(Long id) {

        Appointment appointment = getById(id);

        requireStatus(
                appointment,
                AppointmentStatus.PENDING
        );

        appointment.setStatus(
                AppointmentStatus.CONFIRMED
        );

        return appointmentRepository.save(
                appointment
        );
    }


    @Transactional
    public Appointment start(Long id) {

        Appointment appointment = getById(id);

        requireStatus(
                appointment,
                AppointmentStatus.CONFIRMED
        );

        appointment.setStatus(
                AppointmentStatus.IN_PROGRESS
        );

        return appointmentRepository.save(
                appointment
        );
    }


    @Transactional
    public Appointment complete(Long id) {

        Appointment appointment = getById(id);

        requireStatus(
                appointment,
                AppointmentStatus.IN_PROGRESS
        );

        appointment.setStatus(
                AppointmentStatus.COMPLETED
        );

        return appointmentRepository.save(
                appointment
        );
    }


    @Transactional
    public Appointment cancel(Long id) {

        Appointment appointment = getById(id);

        if (
                appointment.getStatus()
                        != AppointmentStatus.PENDING
                        &&
                        appointment.getStatus()
                                != AppointmentStatus.CONFIRMED
        ) {

            throw new IllegalStateException(
                    "Chỉ có thể hủy lịch đang chờ hoặc đã xác nhận"
            );
        }

        appointment.setStatus(
                AppointmentStatus.CANCELLED
        );

        return appointmentRepository.save(
                appointment
        );
    }


    @Transactional
    public Appointment markNoShow(Long id) {

        Appointment appointment = getById(id);

        requireStatus(
                appointment,
                AppointmentStatus.CONFIRMED
        );

        appointment.setStatus(
                AppointmentStatus.NO_SHOW
        );

        return appointmentRepository.save(
                appointment
        );
    }


    @Transactional
    public Appointment updateAdminNote(
            Long id,
            String adminNote
    ) {

        Appointment appointment = getById(id);

        appointment.setAdminNote(adminNote);

        return appointmentRepository.save(
                appointment
        );
    }

    @Transactional
    public Appointment updateMedicalRecord(
            Long id,
            String diagnosis,
            String prescription,
            LocalDate revisitDate,
            String revisitNotes,
            Boolean requiresDailyFollowup,
            Integer followupDays
    ) {
        Appointment appointment = getById(id);
        appointment.setDiagnosis(diagnosis);
        appointment.setPrescription(prescription);
        appointment.setRevisitDate(revisitDate);
        appointment.setRevisitNotes(revisitNotes);
        appointment.setRequiresDailyFollowup(Boolean.TRUE.equals(requiresDailyFollowup));
        if (followupDays != null && followupDays > 0) {
            appointment.setFollowupDays(followupDays);
        }
        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getFollowupAppointments() {
        return appointmentRepository.findByRequiresDailyFollowupTrueOrderByUpdatedAtDesc();
    }


    private void requireStatus(
            Appointment appointment,
            AppointmentStatus requiredStatus
    ) {

        if (appointment.getStatus()
                != requiredStatus) {

            throw new IllegalStateException(
                    "Trạng thái lịch không hợp lệ. "
                            + "Trạng thái hiện tại: "
                            + appointment.getStatus()
            );
        }
    }
}
