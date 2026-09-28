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

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    private final VetServiceService vetServiceService;

    private final BranchService branchService;


    @GetMapping("/appointments/book")
    public String showBookingForm(
            Model model
    ) {

        model.addAttribute(
                "appointmentRequest",
                new AppointmentRequest()
        );

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

            Appointment appointment =
                    appointmentService
                            .createAppointment(request);

            model.addAttribute(
                    "appointment",
                    appointment
            );

            return "appointments/success";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "bookingError",
                    e.getMessage()
            );

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
