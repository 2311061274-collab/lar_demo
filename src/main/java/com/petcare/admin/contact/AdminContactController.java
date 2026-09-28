package com.petcare.admin.contact;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class AdminContactController {

    private final AdminContactService
            adminContactService;


    /*
     * =========================================================
     * LIST
     * =========================================================
     */

    @GetMapping
    public String list(
            Model model
    ) {

        model.addAttribute(
                "contacts",
                adminContactService.findAll()
        );

        return "admin/contacts/list";
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
                "contact",
                adminContactService.findById(id)
        );

        return "admin/contacts/detail";
    }


    /*
     * =========================================================
     * MARK REPLIED
     * =========================================================
     */

    @PostMapping("/{id}/replied")
    public String markReplied(
            @PathVariable Long id,

            RedirectAttributes redirectAttributes
    ) {

        try {

            adminContactService
                    .markReplied(id);

            redirectAttributes
                    .addFlashAttribute(
                            "successMessage",
                            "Đã đánh dấu liên hệ là đã xử lý."
                    );

        } catch (IllegalArgumentException e) {

            redirectAttributes
                    .addFlashAttribute(
                            "errorMessage",
                            e.getMessage()
                    );
        }

        return "redirect:/admin/contacts/" + id;
    }


    /*
     * =========================================================
     * REPLY
     * =========================================================
     */

    @PostMapping("/{id}/reply")
    public String reply(
            @PathVariable Long id,
            @RequestParam String replyMessage,
            RedirectAttributes redirectAttributes
    ) {

        try {

            adminContactService.reply(id, replyMessage);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã lưu nội dung phản hồi và chuyển trạng thái thành đã xử lý."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/admin/contacts/" + id;
    }
}
