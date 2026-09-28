package com.petcare.admin.branch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdminBranchRequest {

    @NotBlank(
            message = "Tên chi nhánh không được để trống"
    )
    private String name;


    @NotBlank(
            message = "Địa chỉ không được để trống"
    )
    private String address;


    @Pattern(
            regexp = "^$|^[0-9+\\s().-]{8,20}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;


    private Boolean active = true;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }
}
