package com.petcare.admin.news;

import com.petcare.news.News;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final AdminNewsService adminNewsService;


    @GetMapping
    public String list(
            Model model
    ) {

        model.addAttribute(
                "newsList",
                adminNewsService.findAll()
        );

        return "admin/news/list";
    }


    @GetMapping("/create")
    public String createForm(
            Model model
    ) {

        model.addAttribute(
                "newsRequest",
                new AdminNewsRequest()
        );

        prepareCreateForm(model);

        return "admin/news/form";
    }


    @PostMapping
    public String create(
            @Valid
            @ModelAttribute("newsRequest")
            AdminNewsRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            prepareCreateForm(model);

            return "admin/news/form";
        }

        try {

            adminNewsService.create(request);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã tạo bài viết."
            );

            return "redirect:/admin/news";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            prepareCreateForm(model);

            return "admin/news/form";
        }
    }


    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model
    ) {

        News news =
                adminNewsService.findById(id);

        model.addAttribute(
                "newsRequest",
                adminNewsService.toRequest(news)
        );

        prepareEditForm(
                id,
                model
        );

        return "admin/news/form";
    }


    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,

            @Valid
            @ModelAttribute("newsRequest")
            AdminNewsRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            prepareEditForm(
                    id,
                    model
            );

            return "admin/news/form";
        }

        try {

            adminNewsService.update(
                    id,
                    request
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã cập nhật bài viết."
            );

            return "redirect:/admin/news";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            prepareEditForm(
                    id,
                    model
            );

            return "admin/news/form";
        }
    }


    @PostMapping("/{id}/toggle")
    public String togglePublished(
            @PathVariable Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            adminNewsService
                    .togglePublished(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã cập nhật trạng thái bài viết."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/news";
    }


    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            adminNewsService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã xóa bài viết thành công."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/news";
    }


    private void prepareCreateForm(
            Model model
    ) {

        model.addAttribute(
                "pageTitle",
                "Thêm bài viết"
        );

        model.addAttribute(
                "formAction",
                "/admin/news"
        );
    }


    private void prepareEditForm(
            Long id,
            Model model
    ) {

        model.addAttribute(
                "pageTitle",
                "Chỉnh sửa bài viết"
        );

        model.addAttribute(
                "formAction",
                "/admin/news/" + id
        );
    }
}
