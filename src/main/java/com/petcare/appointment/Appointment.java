package com.petcare.appointment;

import com.petcare.branch.Branch;
import com.petcare.customer.Customer;
import com.petcare.pet.Pet;
import com.petcare.service.VetService;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 50
    )
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pet_id",
            nullable = false
    )
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            nullable = false
    )
    private VetService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "branch_id",
            nullable = false
    )
    private Branch branch;

    @Column(
            name = "appointment_date",
            nullable = false
    )
    private LocalDate appointmentDate;

    @Column(
            name = "start_time",
            nullable = false
    )
    private LocalTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "visit_type",
            nullable = false,
            length = 30
    )
    private VisitType visitType;

    @Column(
            name = "visit_address",
            length = 500
    )
    private String visitAddress;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private AppointmentStatus status =
            AppointmentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(
            name = "admin_note",
            columnDefinition = "TEXT"
    )
    private String adminNote;

    @Column(name = "revisit_date")
    private LocalDate revisitDate;

    @Column(name = "revisit_notes", columnDefinition = "TEXT")
    private String revisitNotes;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(name = "requires_daily_followup", nullable = false)
    private Boolean requiresDailyFollowup = false;

    @Column(name = "followup_days", nullable = false)
    private Integer followupDays = 3;

    @Column(name = "last_followup_at")
    private LocalDateTime lastFollowupAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status =
                    AppointmentStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}
