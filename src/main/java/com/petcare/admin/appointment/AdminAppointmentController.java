package com.petcare.admin.appointment;

import com.petcare.appointment.AppointmentStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AdminAppointmentService
            adminAppointmentService;

    private final com.petcare.followup.PetDailyFollowupRepository
            petDailyFollowupRepository;

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        model.addAttribute("appointments", adminAppointmentService.search(keyword, status, date));
        model.addAttribute("statuses", AppointmentStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDate", date);
        return "admin/appointments/list";
    }

    @GetMapping("/followups")
    public String monitorFollowups(Model model) {
        var appointments = adminAppointmentService.getFollowupAppointments();
        LocalDate today = LocalDate.now();

        // Calculate update status for each appointment
        List<AppointmentFollowupDto> dtoList = appointments.stream().map(a -> {
            boolean updatedToday = a.getLastFollowupAt() != null && a.getLastFollowupAt().toLocalDate().equals(today);
            var logs = petDailyFollowupRepository.findByAppointmentIdOrderByDayNumberAsc(a.getId());
            return new AppointmentFollowupDto(a, updatedToday, logs);
        }).toList();

        model.addAttribute("followupItems", dtoList);
        model.addAttribute("today", today);
        return "admin/appointments/followups";
    }

    public record AppointmentFollowupDto(
            com.petcare.appointment.Appointment appointment,
            boolean updatedToday,
            List<com.petcare.followup.PetDailyFollowup> logs
    ) {}

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
                "appointment",
                adminAppointmentService.getById(id)
        );

        model.addAttribute(
                "followupLogs",
                petDailyFollowupRepository.findByAppointmentIdOrderByDayNumberAsc(id)
        );

        model.addAttribute("today", java.time.LocalDate.now());

        return "admin/appointments/detail";
    }

    @PostMapping("/{id}/medical-record")
    public String updateMedicalRecord(
            @PathVariable Long id,
            @RequestParam(value = "diagnosis", required = false) String diagnosis,
            @RequestParam(value = "prescription", required = false) String prescription,
            @RequestParam(value = "revisitDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate revisitDate,
            @RequestParam(value = "revisitNotes", required = false) String revisitNotes,
            @RequestParam(value = "requiresDailyFollowup", required = false) Boolean requiresDailyFollowup,
            @RequestParam(value = "followupDays", defaultValue = "3") Integer followupDays,
            RedirectAttributes redirectAttributes
    ) {
        adminAppointmentService.updateMedicalRecord(
                id,
                diagnosis,
                prescription,
                revisitDate,
                revisitNotes,
                requiresDailyFollowup,
                followupDays
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã lưu kết quả khám, đơn thuốc và lịch hẹn tái khám thành công!"
        );

        return redirectToDetail(id);
    }


    @PostMapping("/{id}/confirm")
    public String confirm(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            adminAppointmentService.confirm(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã xác nhận lịch hẹn."
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return redirectToDetail(id);
    }


    @PostMapping("/{id}/start")
    public String start(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            adminAppointmentService.start(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Lịch hẹn đã chuyển sang trạng thái đang khám."
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return redirectToDetail(id);
    }


    @PostMapping("/{id}/complete")
    public String complete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            adminAppointmentService.complete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã hoàn thành lịch khám."
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return redirectToDetail(id);
    }


    @PostMapping("/{id}/cancel")
    public String cancel(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            adminAppointmentService.cancel(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã hủy lịch hẹn."
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return redirectToDetail(id);
    }


    @PostMapping("/{id}/no-show")
    public String noShow(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            adminAppointmentService.markNoShow(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã đánh dấu khách không đến."
            );

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return redirectToDetail(id);
    }


    @PostMapping("/{id}/note")
    public String updateNote(
            @PathVariable Long id,

            @RequestParam
            String adminNote,

            RedirectAttributes redirectAttributes
    ) {

        adminAppointmentService.updateAdminNote(
                id,
                adminNote
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã lưu ghi chú."
        );

        return redirectToDetail(id);
    }

    @PostMapping("/followups/{followupId}/confirm")
    public String confirmFollowup(
            @PathVariable Long followupId,
            @RequestParam Long appointmentId,
            java.security.Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        com.petcare.followup.PetDailyFollowup log = petDailyFollowupRepository.findById(followupId).orElse(null);
        if (log != null) {
            log.setConfirmedByAdmin(true);
            log.setConfirmedAt(java.time.LocalDateTime.now());
            log.setConfirmedByUsername(principal != null ? principal.getName() : "admin");
            petDailyFollowupRepository.save(log);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận tình trạng sức khỏe ngày " + log.getDayNumber() + " của thú cưng!");
        }
        return redirectToDetail(appointmentId);
    }

    @PostMapping("/followups/{followupId}/unconfirm")
    public String unconfirmFollowup(
            @PathVariable Long followupId,
            @RequestParam Long appointmentId,
            RedirectAttributes redirectAttributes
    ) {
        com.petcare.followup.PetDailyFollowup log = petDailyFollowupRepository.findById(followupId).orElse(null);
        if (log != null) {
            log.setConfirmedByAdmin(false);
            log.setConfirmedAt(null);
            log.setConfirmedByUsername(null);
            petDailyFollowupRepository.save(log);
            redirectAttributes.addFlashAttribute("successMessage", "Đã mở khóa để khách hàng có thể chỉnh sửa lại nhật ký.");
        }
        return redirectToDetail(appointmentId);
    }

    private String redirectToDetail(
            Long id
    ) {

        return "redirect:/admin/appointments/"
                + id;
    }
}
