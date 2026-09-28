package com.petcare.review;

import com.petcare.service.VetServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final VetServiceService vetServiceService;

    @GetMapping
    public String index(Model model) {
        if (!model.containsAttribute("reviewRequest")) {
            model.addAttribute("reviewRequest", new ReviewRequest());
        }
        model.addAttribute("reviews", reviewService.getAllReviews());
        model.addAttribute("services", vetServiceService.getActiveServices());
        return "reviews";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("reviewRequest") ReviewRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.reviewRequest",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("reviewRequest", request);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đánh giá.");
            return "redirect:/reviews";
        }

        try {
            reviewService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã gửi đánh giá! Ý kiến của bạn rất quý giá với PetCare.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/reviews";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @RequestParam String phone,
            @RequestParam String comment,
            @RequestParam(required = false) Integer rating,
            RedirectAttributes redirectAttributes
    ) {
        try {
            reviewService.update(id, phone, comment, rating);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật bình luận của bạn thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            @RequestParam String phone,
            RedirectAttributes redirectAttributes
    ) {
        try {
            reviewService.delete(id, phone);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bình luận của bạn thành công.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/reviews";
    }
}
