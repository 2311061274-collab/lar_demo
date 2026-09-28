package com.petcare.config;

import com.petcare.site.SiteSetting;
import com.petcare.site.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {

    private final SiteSettingService siteSettingService;

    @ModelAttribute("site")
    public SiteSetting populateSiteSetting() {
        return siteSettingService.getCurrent();
    }

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
