package com.petcare.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }

    @GetMapping("/api/chat/history")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestParam String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(chatService.getHistory(sessionId.trim()));
    }

    @PostMapping("/api/chat/send")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> sendMessage(
            @RequestParam String sessionId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) MultipartFile image
    ) {
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                // Chuyển ảnh thành data URL Base64 để hiển thị ngay lập tức, không phụ thuộc file system
                String mimeType = image.getContentType() != null ? image.getContentType() : "image/jpeg";
                String base64 = Base64.getEncoder().encodeToString(image.getBytes());
                imageUrl = "data:" + mimeType + ";base64," + base64;
            } catch (IOException e) {
                // Bỏ qua nếu lỗi đọc file
            }
        }

        List<ChatMessage> updatedHistory = chatService.sendMessage(
                sessionId.trim(),
                message,
                imageUrl
        );

        return ResponseEntity.ok(updatedHistory);
    }
}
