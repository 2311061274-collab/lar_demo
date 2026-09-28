package com.petcare.site;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private final SiteSettingRepository
            siteSettingRepository;


    @Transactional(readOnly = true)
    public SiteSetting getCurrent() {

        return siteSettingRepository
                .findById(1L)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Chưa cấu hình thông tin phòng khám"
                        )
                );
    }
}
