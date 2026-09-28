package com.petcare.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ContactRequest {

    @NotBlank(
            message = "Họ tên không được để trống"
    )
    private String fullName;


    @NotBlank(
            message = "Số điện thoại không được để trống"
    )
    @Pattern(
            regexp = "^(03|05|07|08|09)[0-9]{8}$",
            message = "Số điện thoại không hợp lệ. Vui lòng nhập đúng 10 chữ số thuộc các đầu số Việt Nam (03, 05, 07, 08, 09)."
    )
    private String phone;


    @Email(
            message = "Email không hợp lệ"
    )
    private String email;


    @Size(
            max = 255,
            message = "Tiêu đề quá dài"
    )
    private String subject;


    @NotBlank(
            message = "Nội dung không được để trống"
    )
    @Size(
            max = 5000,
            message = "Nội dung không được vượt quá 5000 ký tự"
    )
    private String message;


    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }
}
