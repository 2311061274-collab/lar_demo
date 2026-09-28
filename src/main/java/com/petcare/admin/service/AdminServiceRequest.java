package com.petcare.admin.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AdminServiceRequest {

    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    private String slug;

    private String summary;

    private String description;

    @DecimalMin(
            value = "0",
            message = "Giá từ không được âm"
    )
    private BigDecimal priceFrom;

    @DecimalMin(
            value = "0",
            message = "Giá đến không được âm"
    )
    private BigDecimal priceTo;

    @Positive(
            message = "Thời gian phải lớn hơn 0"
    )
    private Integer durationMinutes;

    private String imageUrl;

    private Boolean active = true;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }


    public BigDecimal getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(
            BigDecimal priceFrom
    ) {
        this.priceFrom = priceFrom;
    }


    public BigDecimal getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(
            BigDecimal priceTo
    ) {
        this.priceTo = priceTo;
    }


    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(
            Integer durationMinutes
    ) {
        this.durationMinutes = durationMinutes;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl
    ) {
        this.imageUrl = imageUrl;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(
            Boolean active
    ) {
        this.active = active;
    }
}
