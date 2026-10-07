package com.petcare.appointment;

import com.petcare.branch.BranchService;
import com.petcare.service.VetServiceService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    private final VetServiceService vetServiceService;

    private final BranchService branchService;

    private final com.petcare.auth.AppUserRepository appUserRepository;


    @GetMapping("/appointments/book")
    public String showBookingForm(
            @RequestParam(value = "branchId", required = false) Long branchId,
            java.security.Principal principal,
            Model model
    ) {
        AppointmentRequest request = new AppointmentRequest();
        if (branchId != null) {
            request.setBranchId(branchId);
        }

        if (principal != null) {
            try {
                // If logged in, prefill name and phone
                var userOpt = appUserRepository.findByUsername(principal.getName())
                        .or(() -> appUserRepository.findByPhone(principal.getName()));
                userOpt.ifPresent(u -> {
                    if (u.getFullName() != null) request.setCustomerName(u.getFullName());
                    if (u.getPhone() != null) request.setPhone(u.getPhone());
                    if (u.getCustomer() != null) {
                        if (request.getCustomerName() == null) request.setCustomerName(u.getCustomer().getFullName());
                        if (request.getPhone() == null) request.setPhone(u.getCustomer().getPhone());
                        if (u.getCustomer().getEmail() != null) request.setEmail(u.getCustomer().getEmail());
                        if (u.getCustomer().getAddress() != null) request.setVisitAddress(u.getCustomer().getAddress());
                    }
                });
            } catch (Exception ignored) {}
        }

        model.addAttribute("appointmentRequest", request);
        loadFormData(model);
        return "appointments/book";
    }


    @PostMapping("/appointments/book")
    public String bookAppointment(
            @Valid
            @ModelAttribute("appointmentRequest")
            AppointmentRequest request,

            BindingResult bindingResult,

            Model model
    ) {
        if (bindingResult.hasErrors()) {
            loadFormData(model);
            return "appointments/book";
        }

        try {
            Appointment appointment = appointmentService.createAppointment(request);
            model.addAttribute("appointment", appointment);
            return "appointments/success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("bookingError", e.getMessage());
            loadFormData(model);
            return "appointments/book";
        } catch (Exception e) {
            model.addAttribute("bookingError", "Không thể hoàn tất đặt lịch: " + e.getMessage() + ". Quý khách vui lòng thử lại hoặc gọi tổng đài!");
            loadFormData(model);
            return "appointments/book";
        }
    }


    private void loadFormData(
            Model model
    ) {

        model.addAttribute(
                "services",
                vetServiceService.getActiveServices()
        );

        model.addAttribute(
                "branches",
                branchService.getActiveBranches()
        );
    }
}
