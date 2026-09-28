package com.petcare.site;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SiteController {

    private final SiteSettingService
            siteSettingService;


    @GetMapping("/about")
    public String about(
            Model model
    ) {

        model.addAttribute(
                "site",
                siteSettingService.getCurrent()
        );

        return "about";
    }
}
