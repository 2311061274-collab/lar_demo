package com.petcare.appointment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    /*
     * =========================================================
     * PUBLIC BOOKING
     * =========================================================
     */

    /**
     * Tìm lịch hẹn theo mã.
     *
     * Ví dụ:
     * APT-20260913-A12BCD
     */
    Optional<Appointment> findByCode(String code);


    /**
     * Kiểm tra một khung giờ tại một chi nhánh
     * đã có appointment đang hoạt động hay chưa.
     *
     * Ví dụ blocking statuses:
     * PENDING
     * CONFIRMED
     * IN_PROGRESS
     */
    boolean existsByBranch_IdAndAppointmentDateAndStartTimeAndStatusIn(
            Long branchId,
            LocalDate appointmentDate,
            LocalTime startTime,
            Collection<AppointmentStatus> statuses
    );


    /*
     * =========================================================
     * ADMIN - SEARCH / FILTER
     * =========================================================
     */

    /**
     * Admin tìm kiếm lịch hẹn.
     *
     * Keyword:
     * - mã lịch
     * - tên khách
     * - số điện thoại
     * - tên thú cưng
     *
     * Filter:
     * - status
     * - ngày khám
     */
    @Query("""
            SELECT a
            FROM Appointment a

            JOIN FETCH a.customer c
            JOIN FETCH a.pet p
            JOIN FETCH a.service s
            JOIN FETCH a.branch b

            WHERE
                (
                    :keyword IS NULL
                    OR :keyword = ''

                    OR LOWER(a.code)
                        LIKE LOWER(
                            CONCAT('%', :keyword, '%')
                        )

                    OR LOWER(c.fullName)
                        LIKE LOWER(
                            CONCAT('%', :keyword, '%')
                        )

                    OR c.phone
                        LIKE CONCAT('%', :keyword, '%')

                    OR LOWER(p.name)
                        LIKE LOWER(
                            CONCAT('%', :keyword, '%')
                        )
                )

                AND a.status =
                    COALESCE(
                        :status,
                        a.status
                    )

                AND a.appointmentDate =
                    COALESCE(
                        :appointmentDate,
                        a.appointmentDate
                    )

            ORDER BY
                a.appointmentDate DESC,
                a.startTime DESC
            """)
    List<Appointment> searchAdminAppointments(
            @Param("keyword")
            String keyword,

            @Param("status")
            AppointmentStatus status,

            @Param("appointmentDate")
            LocalDate appointmentDate
    );


    /*
     * =========================================================
     * ADMIN - APPOINTMENT DETAIL
     * =========================================================
     */

    /**
     * Lấy chi tiết appointment.
     *
     * EntityGraph load luôn:
     * - customer
     * - pet
     * - service
     * - branch
     */
    @EntityGraph(
            attributePaths = {
                    "customer",
                    "pet",
                    "service",
                    "branch"
            }
    )
    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.id = :id
            """)
    Optional<Appointment> findDetailById(
            @Param("id")
            Long id
    );


    /*
     * =========================================================
     * ADMIN DASHBOARD - COUNTERS
     * =========================================================
     */

    /**
     * Đếm appointment theo trạng thái.
     */
    long countByStatus(
            AppointmentStatus status
    );


    /**
     * Đếm tổng appointment của một ngày.
     *
     * Dùng cho:
     * "Tổng lịch hôm nay"
     */
    long countByAppointmentDate(
            LocalDate appointmentDate
    );


    /**
     * Đếm appointment theo:
     *
     * ngày + trạng thái
     */
    long countByAppointmentDateAndStatus(
            LocalDate appointmentDate,
            AppointmentStatus status
    );


    /*
     * =========================================================
     * ADMIN DASHBOARD - UPCOMING APPOINTMENTS
     * =========================================================
     */

    /**
     * Lấy các lịch thực sự chưa xảy ra.
     *
     * Điều kiện:
     *
     * ngày > hôm nay
     *
     * HOẶC
     *
     * ngày = hôm nay
     * và giờ >= giờ hiện tại
     *
     * Chỉ lấy các trạng thái còn hoạt động:
     * PENDING
     * CONFIRMED
     * IN_PROGRESS
     *
     * Sau đó sort:
     * ngày ASC
     * giờ ASC
     */
    @EntityGraph(
            attributePaths = {
                    "customer",
                    "pet",
                    "service",
                    "branch"
            }
    )
    @Query("""
            SELECT a
            FROM Appointment a

            WHERE
                a.status IN :statuses

                AND
                (
                    a.appointmentDate > :today

                    OR
                    (
                        a.appointmentDate = :today
                        AND a.startTime >= :now
                    )
                )

            ORDER BY
                a.appointmentDate ASC,
                a.startTime ASC
            """)
    List<Appointment> findUpcomingAppointments(
            @Param("today")
            LocalDate today,

            @Param("now")
            LocalTime now,

            @Param("statuses")
            Collection<AppointmentStatus> statuses
    );
    @EntityGraph(
            attributePaths = {
                    "customer",
                    "pet",
                    "service",
                    "branch"
            }
    )
    List<Appointment>
    findByCustomer_IdOrderByAppointmentDateDescStartTimeDesc(
            Long customerId
    );
    /*
     * =========================================================
     * ADMIN PET HISTORY
     * =========================================================
     */

    @EntityGraph(
            attributePaths = {
                    "customer",
                    "pet",
                    "service",
                    "branch"
            }
    )
    List<Appointment>
    findByPet_IdOrderByAppointmentDateDescStartTimeDesc(
            Long petId
    );

    boolean existsByPet_Id(Long petId);

    boolean existsByService_Id(Long serviceId);

    List<Appointment> findByBranch_IdAndAppointmentDateAndStatusIn(
            Long branchId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses
    );

    @EntityGraph(attributePaths = {"customer", "pet", "service", "branch"})
    List<Appointment> findByCustomer_IdAndRevisitDateIsNotNullOrderByRevisitDateAsc(Long customerId);

    @EntityGraph(attributePaths = {"customer", "pet", "service", "branch"})
    List<Appointment> findByRequiresDailyFollowupTrueOrderByUpdatedAtDesc();
}
