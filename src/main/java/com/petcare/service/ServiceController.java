package com.petcare.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ServiceController {

    private final VetServiceService vetServiceService;


    /*
     * =========================================
     * PUBLIC - DANH SÁCH DỊCH VỤ
     * =========================================
     */

    @GetMapping("/services")
    public String listServices(
            Model model
    ) {

        model.addAttribute(
                "services",
                vetServiceService.getActiveServices()
        );

        return "services/list";
    }


    /*
     * =========================================
     * PUBLIC - CHI TIẾT DỊCH VỤ
     * =========================================
     */

    @GetMapping("/services/{slug}")
    public String serviceDetail(
            @PathVariable String slug,
            Model model
    ) {

        VetService service =
                vetServiceService.getBySlug(slug);

        model.addAttribute(
                "service",
                service
        );

        return "services/detail";
    }
}
