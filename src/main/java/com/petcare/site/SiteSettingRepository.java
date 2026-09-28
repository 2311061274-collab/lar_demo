package com.petcare.site;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingRepository
        extends JpaRepository<SiteSetting, Long> {
}
