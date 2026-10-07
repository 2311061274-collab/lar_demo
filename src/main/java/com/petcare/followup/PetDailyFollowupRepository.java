package com.petcare.followup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetDailyFollowupRepository extends JpaRepository<PetDailyFollowup, Long> {

    List<PetDailyFollowup> findByAppointmentIdOrderByDayNumberAsc(Long appointmentId);

    List<PetDailyFollowup> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<PetDailyFollowup> findByPetIdOrderByDayNumberAsc(Long petId);

    java.util.Optional<PetDailyFollowup> findByAppointmentIdAndDayNumber(Long appointmentId, Integer dayNumber);

    @Query("SELECT f FROM PetDailyFollowup f WHERE f.appointment.id = :appointmentId ORDER BY f.dayNumber DESC")
    List<PetDailyFollowup> findLatestByAppointment(@Param("appointmentId") Long appointmentId);
}
