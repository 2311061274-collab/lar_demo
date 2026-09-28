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
    public Review create(ReviewRequest request) {
        Review review = new Review();
        review.setCustomerName(request.getCustomerName().trim());
        review.setPhone(request.getPhone().trim());
        review.setServiceName(request.getServiceName() != null ? request.getServiceName().trim() : null);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());
        return reviewRepository.save(review);
    }

    @Transactional
    public Review update(Long id, String phone, String comment, Integer rating) {
        String cleanPhone = phone != null ? phone.trim() : "";
        Review review = reviewRepository.findByIdAndPhone(id, cleanPhone)
                .orElseThrow(() -> new IllegalArgumentException("Số điện thoại không khớp với người đã gửi bình luận này!"));

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
    public void delete(Long id, String phone) {
        String cleanPhone = phone != null ? phone.trim() : "";
        Review review = reviewRepository.findByIdAndPhone(id, cleanPhone)
                .orElseThrow(() -> new IllegalArgumentException("Số điện thoại không khớp với người đã gửi bình luận này!"));

        reviewRepository.delete(review);
    }
}
