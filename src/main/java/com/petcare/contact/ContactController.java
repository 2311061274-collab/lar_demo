package com.petcare.contact;

import com.petcare.site.SiteSettingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    private final SiteSettingService
            siteSettingService;


    @GetMapping("/contact")
    public String form(
            Model model
    ) {

        if (!model.containsAttribute(
                "contactRequest"
        )) {

            model.addAttribute(
                    "contactRequest",
                    new ContactRequest()
            );
        }

        model.addAttribute(
                "site",
                siteSettingService.getCurrent()
        );

        return "contact";
    }


    @PostMapping("/contact")
    public String submit(
            @Valid
            @ModelAttribute("contactRequest")
            ContactRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "site",
                    siteSettingService.getCurrent()
            );

            return "contact";
        }

        contactService.create(request);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Cảm ơn bạn. PetCare đã nhận được thông tin liên hệ."
        );

        return "redirect:/contact";
    }
}
