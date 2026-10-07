package com.petcare.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Review create(ReviewRequest request, String username) {
        Review review = new Review();
        review.setCustomerName(request.getCustomerName().trim());
        review.setPhone(request.getPhone().trim());
        review.setServiceName(request.getServiceName() != null ? request.getServiceName().trim() : null);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());
        review.setUsername(username != null ? username.trim().toLowerCase() : null);
        return reviewRepository.save(review);
    }

    @Transactional
    public Review update(Long id, String username, String phone, String comment, Integer rating) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận đánh giá!"));

        // KIỂM TRA QUYỀN SỞ HỮU: CHỈ TÀI KHOẢN ĐÃ ĐĂNG MỚI ĐƯỢC PHÉP SỬA
        if (review.getUsername() != null && !review.getUsername().isBlank()) {
            if (username == null || !review.getUsername().equalsIgnoreCase(username.trim())) {
                throw new IllegalArgumentException("Bạn không thể chỉnh sửa bình luận của người khác! Quản trị viên cũng không thể sửa đánh giá của khách hàng.");
            }
        } else {
            // Trường hợp review cũ chưa gán username: dùng số điện thoại xác thực
            String cleanPhone = phone != null ? phone.trim() : "";
            if (!review.getPhone().equals(cleanPhone)) {
                throw new IllegalArgumentException("Số điện thoại không khớp với người đã gửi bình luận này!");
            }
        }

        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Nội dung bình luận không được để trống.");
        }

        review.setComment(comment.trim());
        if (rating != null && rating >= 1 && rating <= 5) {
            review.setRating(rating);
        }

        return reviewRepository.save(review);
    }

    @Transactional
    public void delete(Long id, String username, String phone) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận đánh giá!"));

        boolean isAuthor = (username != null && review.getUsername() != null && review.getUsername().equalsIgnoreCase(username.trim()))
                || (phone != null && review.getPhone().equals(phone.trim()));

        if (!isAuthor) {
            throw new IllegalArgumentException("Bạn chỉ có thể xóa bình luận do chính tài khoản của bạn đăng tải!");
        }

        reviewRepository.delete(review);
    }
}
