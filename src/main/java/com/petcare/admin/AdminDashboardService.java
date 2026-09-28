package com.petcare.admin;

import com.petcare.appointment.Appointment;
import com.petcare.appointment.AppointmentRepository;
import com.petcare.appointment.AppointmentStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AppointmentRepository appointmentRepository;


    @Transactional(readOnly = true)
    public long getTodayTotal() {

        return appointmentRepository
                .countByAppointmentDate(
                        LocalDate.now()
                );
    }


    @Transactional(readOnly = true)
    public long getTodayPending() {

        return countTodayByStatus(
                AppointmentStatus.PENDING
        );
    }


    @Transactional(readOnly = true)
    public long getTodayConfirmed() {

        return countTodayByStatus(
                AppointmentStatus.CONFIRMED
        );
    }


    @Transactional(readOnly = true)
    public long getTodayInProgress() {

        return countTodayByStatus(
                AppointmentStatus.IN_PROGRESS
        );
    }


    @Transactional(readOnly = true)
    public long getTodayCompleted() {

        return countTodayByStatus(
                AppointmentStatus.COMPLETED
        );
    }


    @Transactional(readOnly = true)
    public long getTodayCancelled() {

        return countTodayByStatus(
                AppointmentStatus.CANCELLED
        );
    }


    @Transactional(readOnly = true)
    public long getTodayNoShow() {

        return countTodayByStatus(
                AppointmentStatus.NO_SHOW
        );
    }


    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments() {

        List<AppointmentStatus> activeStatuses =
                List.of(
                        AppointmentStatus.PENDING,
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.IN_PROGRESS
                );

        return appointmentRepository
                .findUpcomingAppointments(
                        LocalDate.now(),
                        LocalTime.now(),
                        activeStatuses
                )
                .stream()
                .limit(10)
                .toList();
    }


    private long countTodayByStatus(
            AppointmentStatus status
    ) {

        return appointmentRepository
                .countByAppointmentDateAndStatus(
                        LocalDate.now(),
                        status
                );
    }
}
