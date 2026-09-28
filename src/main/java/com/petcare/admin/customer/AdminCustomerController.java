package com.petcare.admin.customer;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final AdminCustomerService
            adminCustomerService;


    /*
     * =========================================
     * CUSTOMER LIST
     * =========================================
     */

    @GetMapping
    public String list(
            @RequestParam(
                    required = false
            )
            String keyword,

            Model model
    ) {

        model.addAttribute(
                "customers",
                adminCustomerService
                        .search(keyword)
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "admin/customers/list";
    }


    /*
     * =========================================
     * CUSTOMER DETAIL
     * =========================================
     */

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
                "customer",
                adminCustomerService
                        .findById(id)
        );

        model.addAttribute(
                "pets",
                adminCustomerService
                        .findPets(id)
        );

        model.addAttribute(
                "appointments",
                adminCustomerService
                        .findAppointments(id)
        );

        return "admin/customers/detail";
    }
}
