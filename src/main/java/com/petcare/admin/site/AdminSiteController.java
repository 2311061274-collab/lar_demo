package com.petcare.admin.site;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSiteController {

    private final AdminSiteService
            adminSiteService;


    @GetMapping
    public String form(
            Model model
    ) {

        model.addAttribute(
                "siteRequest",
                adminSiteService.toRequest(
                        adminSiteService.getCurrent()
                )
        );

        return "admin/settings/form";
    }


    @PostMapping
    public String update(
            @Valid
            @ModelAttribute("siteRequest")
            AdminSiteRequest request,

            BindingResult bindingResult,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            return "admin/settings/form";
        }

        adminSiteService.update(request);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã cập nhật thông tin website."
        );

        return "redirect:/admin/settings";
    }
}
