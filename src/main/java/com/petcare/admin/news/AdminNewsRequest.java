package com.petcare.admin.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminNewsRequest {

    @NotBlank(
            message = "Tiêu đề không được để trống"
    )
    private String title;


    @NotBlank(
            message = "Slug không được để trống"
    )
    private String slug;


    @Size(
            max = 1000,
            message = "Mô tả ngắn không được vượt quá 1000 ký tự"
    )
    private String summary;


    @NotBlank(
            message = "Nội dung bài viết không được để trống"
    )
    private String content;


    private String imageUrl;


    private Boolean published = false;


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
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


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Boolean getPublished() {
        return published;
    }


    public void setPublished(Boolean published) {
        this.published = published;
    }
}
