package com.petcare.admin.branch;

import com.petcare.branch.Branch;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/branches")
@RequiredArgsConstructor
public class AdminBranchController {

    private final AdminBranchService
            adminBranchService;


    /*
     * =========================================
     * LIST
     * =========================================
     */

    @GetMapping
    public String list(
            Model model
    ) {

        model.addAttribute(
                "branches",
                adminBranchService.findAll()
        );

        return "admin/branches/list";
    }


    /*
     * =========================================
     * CREATE FORM
     * =========================================
     */

    @GetMapping("/create")
    public String createForm(
            Model model
    ) {

        model.addAttribute(
                "branchRequest",
                new AdminBranchRequest()
        );

        prepareCreateForm(model);

        return "admin/branches/form";
    }


    /*
     * =========================================
     * CREATE
     * =========================================
     */

    @PostMapping
    public String create(
            @Valid
            @ModelAttribute("branchRequest")
            AdminBranchRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            prepareCreateForm(model);

            return "admin/branches/form";
        }

        adminBranchService.create(request);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã thêm chi nhánh thành công."
        );

        return "redirect:/admin/branches";
    }


    /*
     * =========================================
     * EDIT FORM
     * =========================================
     */

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model
    ) {

        Branch branch =
                adminBranchService.findById(id);

        model.addAttribute(
                "branchRequest",
                adminBranchService
                        .toRequest(branch)
        );

        prepareEditForm(
                id,
                model
        );

        return "admin/branches/form";
    }


    /*
     * =========================================
     * UPDATE
     * =========================================
     */

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,

            @Valid
            @ModelAttribute("branchRequest")
            AdminBranchRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            prepareEditForm(
                    id,
                    model
            );

            return "admin/branches/form";
        }

        adminBranchService.update(
                id,
                request
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã cập nhật chi nhánh."
        );

        return "redirect:/admin/branches";
    }


    /*
     * =========================================
     * ENABLE / DISABLE
     * =========================================
     */

    @PostMapping("/{id}/toggle")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            adminBranchService.toggleActive(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã cập nhật trạng thái chi nhánh."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/branches";
    }


    private void prepareCreateForm(
            Model model
    ) {

        model.addAttribute(
                "pageTitle",
                "Thêm chi nhánh"
        );

        model.addAttribute(
                "formAction",
                "/admin/branches"
        );
    }


    private void prepareEditForm(
            Long id,
            Model model
    ) {

        model.addAttribute(
                "pageTitle",
                "Chỉnh sửa chi nhánh"
        );

        model.addAttribute(
                "formAction",
                "/admin/branches/" + id
        );
    }
}
