package com.petcare.home;

import com.petcare.branch.BranchService;
import com.petcare.news.NewsService;
import com.petcare.service.VetServiceService;
import com.petcare.site.SiteSettingService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.petcare.appointment.AppointmentRequest;
import com.petcare.appointment.VisitType;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VetServiceService vetServiceService;

    private final NewsService newsService;

    private final BranchService branchService;

    private final SiteSettingService siteSettingService;


    @GetMapping("/")
    public String home(Model model) {

        var activeServices = vetServiceService.getActiveServices();

        model.addAttribute(
                "services",
                activeServices
                        .stream()
                        .limit(8)
                        .toList()
        );

        model.addAttribute(
                "allServices",
                activeServices
        );

        model.addAttribute(
                "newsList",
                newsService
                        .getPublishedNews()
                        .stream()
                        .limit(3)
                        .toList()
        );

        model.addAttribute(
                "branches",
                branchService.getActiveBranches()
        );

        AppointmentRequest defaultBooking = new AppointmentRequest();
        defaultBooking.setAppointmentDate(LocalDate.now());
        defaultBooking.setStartTime(LocalTime.of(9, 0));
        defaultBooking.setVisitType(VisitType.AT_CLINIC);
        defaultBooking.setSpecies("Chó");
        model.addAttribute("appointmentRequest", defaultBooking);

        return "home";
    }


    @GetMapping("/branches")
    public String branches(Model model) {

        model.addAttribute(
                "branches",
                branchService.getActiveBranches()
        );

        return "branches";
    }
}
