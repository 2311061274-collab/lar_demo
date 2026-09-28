package com.petcare.admin.site;

import com.petcare.site.SiteSetting;
import com.petcare.site.SiteSettingRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSiteService {

    private final SiteSettingRepository
            siteSettingRepository;


    @Transactional(readOnly = true)
    public SiteSetting getCurrent() {

        return siteSettingRepository
                .findById(1L)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Không tìm thấy cấu hình website"
                        )
                );
    }


    public AdminSiteRequest toRequest(
            SiteSetting site
    ) {

        AdminSiteRequest request =
                new AdminSiteRequest();

        request.setClinicName(site.getClinicName());
        request.setTagline(site.getTagline());
        request.setAboutTitle(site.getAboutTitle());
        request.setAboutContent(site.getAboutContent());
        request.setAddress(site.getAddress());
        request.setPhone(site.getPhone());
        request.setEmail(site.getEmail());
        request.setOpeningHours(site.getOpeningHours());
        request.setFacebookUrl(site.getFacebookUrl());
        request.setMapUrl(site.getMapUrl());

        return request;
    }


    @Transactional
    public void update(
            AdminSiteRequest request
    ) {

        SiteSetting site = getCurrent();

        site.setClinicName(
                request.getClinicName().trim()
        );

        site.setTagline(request.getTagline());

        site.setAboutTitle(
                request.getAboutTitle()
        );

        site.setAboutContent(
                request.getAboutContent()
        );

        site.setAddress(request.getAddress());
        site.setPhone(request.getPhone());
        site.setEmail(request.getEmail());

        site.setOpeningHours(
                request.getOpeningHours()
        );

        site.setFacebookUrl(
                request.getFacebookUrl()
        );

        site.setMapUrl(
                request.getMapUrl()
        );

        siteSettingRepository.save(site);
    }
}
