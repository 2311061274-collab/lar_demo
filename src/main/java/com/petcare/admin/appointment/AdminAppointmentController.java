package com.petcare.admin.appointment;

import com.petcare.appointment.AppointmentStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AdminAppointmentService
            adminAppointmentService;


    @GetMapping
    public String list(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            AppointmentStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date,

            Model model
    ) {

        model.addAttribute(
                "appointments",
                adminAppointmentService.search(
                        keyword,
                        status,
                        date
                )
        );

        model.addAttribute(
                "statuses",
                AppointmentStatus.values()
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "selectedStatus",
                status
        );

        model.addAttribute(
                "selectedDate",
                date
        );

        return "admin/appointments/list";
    }


    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
                "appointment",
                adminAppointmentService.getById(id)
        );

        return "admin/appointments/detail";
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


    private String redirectToDetail(
            Long id
    ) {

        return "redirect:/admin/appointments/"
                + id;
    }
}
