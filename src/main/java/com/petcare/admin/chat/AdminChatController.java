package com.petcare.admin.chat;

import com.petcare.chat.ChatMessage;
import com.petcare.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class AdminChatController {

    private final ChatService chatService;

    @GetMapping("/admin/chat")
    public String chatAdminPage(
            @RequestParam(required = false) String sessionId,
            Model model
    ) {
        List<ChatMessage> sessions = chatService.getRecentChatSessions();
        String selectedSessionId = sessionId;

        if ((selectedSessionId == null || selectedSessionId.isBlank()) && !sessions.isEmpty()) {
            selectedSessionId = sessions.get(0).getSessionId();
        }

        List<ChatMessage> messages = Collections.emptyList();
        if (selectedSessionId != null && !selectedSessionId.isBlank()) {
            messages = chatService.getHistory(selectedSessionId);
        }

        model.addAttribute("sessions", sessions);
        model.addAttribute("currentSessionId", selectedSessionId);
        model.addAttribute("messages", messages);
        model.addAttribute("requestURI", "/admin/chat");

        return "admin/chat/index";
    }

    @PostMapping("/admin/chat/reply")
    public String replyPost(
            @RequestParam String sessionId,
            @RequestParam String message,
            RedirectAttributes redirectAttributes
    ) {
        try {
            chatService.sendAdminReply(sessionId, message);
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi phản hồi tới khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/chat?sessionId=" + sessionId;
    }

    // REST API for dynamic polling / instant messaging without full page refresh
    @GetMapping("/admin/api/chat/sessions")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getRecentSessions() {
        return ResponseEntity.ok(chatService.getRecentChatSessions());
    }

    @GetMapping("/admin/api/chat/history")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getSessionHistory(@RequestParam String sessionId) {
        return ResponseEntity.ok(chatService.getHistory(sessionId.trim()));
    }

    @PostMapping("/admin/api/chat/reply")
    @ResponseBody
    public ResponseEntity<?> sendAjaxReply(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String text = payload.get("message");
        if (sessionId == null || sessionId.isBlank() || text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Dữ liệu không hợp lệ"));
        }
        ChatMessage sent = chatService.sendAdminReply(sessionId, text);
        return ResponseEntity.ok(sent);
    }
}
