package com.petcare.admin.service;

import com.petcare.service.VetService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final AdminServiceService adminServiceService;


    @GetMapping
    public String list(
            Model model
    ) {

        model.addAttribute(
                "services",
                adminServiceService.findAll()
        );

        return "admin/services/list";
    }


    @GetMapping("/create")
    public String createForm(
            Model model
    ) {

        model.addAttribute(
                "serviceRequest",
                new AdminServiceRequest()
        );

        prepareCreateForm(model);

        return "admin/services/form";
    }


    @PostMapping
    public String create(
            @Valid
            @ModelAttribute("serviceRequest")
            AdminServiceRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            prepareCreateForm(model);

            return "admin/services/form";
        }

        try {

            adminServiceService.create(request);

            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Đã thêm dịch vụ thành công."
                    );

            return "redirect:/admin/services";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            prepareCreateForm(model);

            return "admin/services/form";
        }
    }


    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model
    ) {

        VetService service =
                adminServiceService.findById(id);

        model.addAttribute(
                "serviceRequest",
                adminServiceService
                        .toRequest(service)
        );

        prepareEditForm(
                id,
                model
        );

        return "admin/services/form";
    }


    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,

            @Valid
            @ModelAttribute("serviceRequest")
            AdminServiceRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            prepareEditForm(
                    id,
                    model
            );

            return "admin/services/form";
        }

        try {

            adminServiceService.update(
                    id,
                    request
            );

            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Đã cập nhật dịch vụ."
                    );

            return "redirect:/admin/services";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            prepareEditForm(
                    id,
                    model
            );

            return "admin/services/form";
        }
    }


    @PostMapping("/{id}/toggle")
    public String toggle(
            @PathVariable Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            adminServiceService.toggleActive(id);

            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Đã cập nhật trạng thái dịch vụ."
                    );

        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "errorMessage",
                            e.getMessage()
                    );
        }

        return "redirect:/admin/services";
    }


    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            adminServiceService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã xóa dịch vụ thành công."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/services";
    }


    private void prepareCreateForm(
            Model model
    ) {

        model.addAttribute(
                "pageTitle",
                "Thêm dịch vụ"
        );

        model.addAttribute(
                "formAction",
                "/admin/services"
        );
    }


    private void prepareEditForm(
            Long id,
            Model model
    ) {

        model.addAttribute(
                "pageTitle",
                "Chỉnh sửa dịch vụ"
        );

        model.addAttribute(
                "formAction",
                "/admin/services/" + id
        );
    }
}
