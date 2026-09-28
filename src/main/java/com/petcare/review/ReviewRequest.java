package com.petcare.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    @NotBlank(message = "Vui lòng nhập họ tên của bạn")
    private String customerName;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @jakarta.validation.constraints.Pattern(
            regexp = "^(03|05|07|08|09)[0-9]{8}$",
            message = "Số điện thoại không hợp lệ. Vui lòng nhập đúng 10 chữ số thuộc các đầu số Việt Nam (03, 05, 07, 08, 09)."
    )
    private String phone;

    private String serviceName;

    @NotNull(message = "Vui lòng chọn số sao đánh giá")
    @Min(value = 1, message = "Đánh giá tối thiểu 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa 5 sao")
    private Integer rating = 5;

    @NotBlank(message = "Vui lòng nhập nội dung đánh giá/bình luận")
    private String comment;
}
