package com.petcare.admin;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminDashboardService
            adminDashboardService;


    @GetMapping("/admin/login")
    public String login() {

        return "admin/login";
    }


    @GetMapping("/admin/dashboard")
    public String dashboard(
            Model model
    ) {

        model.addAttribute(
                "todayTotal",
                adminDashboardService.getTodayTotal()
        );

        model.addAttribute(
                "pendingCount",
                adminDashboardService.getTodayPending()
        );

        model.addAttribute(
                "confirmedCount",
                adminDashboardService.getTodayConfirmed()
        );

        model.addAttribute(
                "inProgressCount",
                adminDashboardService.getTodayInProgress()
        );

        model.addAttribute(
                "completedCount",
                adminDashboardService.getTodayCompleted()
        );

        model.addAttribute(
                "cancelledCount",
                adminDashboardService.getTodayCancelled()
        );

        model.addAttribute(
                "noShowCount",
                adminDashboardService.getTodayNoShow()
        );

        model.addAttribute(
                "allTotal",
                adminDashboardService.getAllTotal()
        );

        model.addAttribute(
                "allPending",
                adminDashboardService.getAllPending()
        );

        model.addAttribute(
                "allConfirmed",
                adminDashboardService.getAllConfirmed()
        );

        model.addAttribute(
                "allInProgress",
                adminDashboardService.getAllInProgress()
        );

        model.addAttribute(
                "allCompleted",
                adminDashboardService.getAllCompleted()
        );

        model.addAttribute(
                "allCancelled",
                adminDashboardService.getAllCancelled()
        );

        model.addAttribute(
                "allNoShow",
                adminDashboardService.getAllNoShow()
        );

        model.addAttribute(
                "upcomingAppointments",
                adminDashboardService
                        .getUpcomingAppointments()
        );

        return "admin/dashboard";
    }
}
