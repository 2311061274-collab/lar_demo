package com.petcare.followup;

import com.petcare.appointment.Appointment;
import com.petcare.customer.Customer;
import com.petcare.pet.Pet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_daily_followups")
@Getter
@Setter
@NoArgsConstructor
public class PetDailyFollowup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "eating_status", length = 50)
    private String eatingStatus; // Ăn tốt, Ăn ít, Bỏ ăn...

    @Column(name = "temperature_status", length = 50)
    private String temperatureStatus; // Bình thường, Ấm nhẹ, Sốt...

    @Column(name = "energy_status", length = 50)
    private String energyStatus; // Hoạt bát, Bình thường, Mệt mỏi/ủ rũ...

    @Column(name = "symptoms_notes", nullable = false, columnDefinition = "TEXT")
    private String symptomsNotes; // Vết mổ, phân, biểu hiện, thuốc đã uống...

    @Column(name = "clinic_feedback", columnDefinition = "TEXT")
    private String clinicFeedback; // Phản hồi / lời dặn từ bác sĩ

    @Column(name = "confirmed_by_admin", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean confirmedByAdmin = false; // Bác sĩ / Admin đã xác nhận kiểm tra

    public boolean isConfirmedByAdmin() {
        return Boolean.TRUE.equals(confirmedByAdmin);
    }

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmed_by_username", length = 100)
    private String confirmedByUsername;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (logDate == null) {
            logDate = LocalDate.now();
        }
    }
}
