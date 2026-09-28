package com.petcare.admin.pet;

import com.petcare.pet.Pet;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pets")
@RequiredArgsConstructor
public class AdminPetController {

    private final AdminPetService adminPetService;


    /*
     * =========================================================
     * LIST / SEARCH
     * =========================================================
     */

    @GetMapping
    public String list(
            @RequestParam(
                    required = false
            )
            String keyword,

            Model model
    ) {

        model.addAttribute(
                "pets",
                adminPetService.search(
                        keyword
                )
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "admin/pets/list";
    }


    /*
     * =========================================================
     * DETAIL
     * =========================================================
     */

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
                "pet",
                adminPetService.findById(id)
        );

        model.addAttribute(
                "appointments",
                adminPetService
                        .findAppointments(id)
        );

        return "admin/pets/detail";
    }


    /*
     * =========================================================
     * CREATE FORM
     * =========================================================
     */

    @GetMapping("/create")
    public String createForm(
            @RequestParam(required = false) Long customerId,
            Model model
    ) {

        AdminPetRequest request = new AdminPetRequest();

        if (customerId != null) {
            request.setCustomerId(customerId);
        }

        model.addAttribute(
                "petRequest",
                request
        );

        prepareCreateForm(model);

        return "admin/pets/form";
    }


    /*
     * =========================================================
     * CREATE
     * =========================================================
     */

    @PostMapping
    public String create(
            @Valid
            @ModelAttribute("petRequest")
            AdminPetRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (request.getCustomerId() == null) {
            bindingResult.rejectValue(
                    "customerId",
                    "error.petRequest",
                    "Vui lòng chọn chủ nuôi"
            );
        }

        if (bindingResult.hasErrors()) {

            prepareCreateForm(model);

            return "admin/pets/form";
        }

        try {

            Pet pet = adminPetService.create(request);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã thêm hồ sơ thú cưng thành công."
            );

            return "redirect:/admin/pets/" + pet.getId();

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            prepareCreateForm(model);

            return "admin/pets/form";
        }
    }


    /*
     * =========================================================
     * EDIT FORM
     * =========================================================
     */

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model
    ) {

        Pet pet =
                adminPetService.findById(id);

        model.addAttribute(
                "pet",
                pet
        );

        model.addAttribute(
                "petRequest",
                adminPetService.toRequest(pet)
        );

        prepareEditForm(id, model);

        return "admin/pets/form";
    }


    /*
     * =========================================================
     * UPDATE
     * =========================================================
     */

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,

            @Valid
            @ModelAttribute("petRequest")
            AdminPetRequest request,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "pet",
                    adminPetService.findById(id)
            );

            prepareEditForm(id, model);

            return "admin/pets/form";
        }

        try {

            adminPetService.update(
                    id,
                    request
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã cập nhật thông tin thú cưng."
            );

            return "redirect:/admin/pets/" + id;

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            model.addAttribute(
                    "pet",
                    adminPetService.findById(id)
            );

            prepareEditForm(id, model);

            return "admin/pets/form";
        }
    }


    /*
     * =========================================================
     * DELETE
     * =========================================================
     */

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            adminPetService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã xóa hồ sơ thú cưng thành công."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "redirect:/admin/pets/" + id;
        }

        return "redirect:/admin/pets";
    }


    private void prepareCreateForm(Model model) {

        model.addAttribute(
                "pageTitle",
                "Thêm hồ sơ thú cưng"
        );

        model.addAttribute(
                "formAction",
                "/admin/pets"
        );

        model.addAttribute(
                "customers",
                adminPetService.findAllCustomers()
        );
    }


    private void prepareEditForm(Long id, Model model) {

        model.addAttribute(
                "pageTitle",
                "Chỉnh sửa thú cưng"
        );

        model.addAttribute(
                "formAction",
                "/admin/pets/" + id
        );
    }
}
