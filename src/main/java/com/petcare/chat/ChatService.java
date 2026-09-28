package com.petcare.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final GeminiAiService geminiAiService;

    @Transactional
    public List<ChatMessage> getHistory(String sessionId) {
        List<ChatMessage> list = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        if (list.isEmpty()) {
            // Lời chào mở đầu tự động
            ChatMessage welcome = new ChatMessage();
            welcome.setSessionId(sessionId);
            welcome.setSender("VET");
            welcome.setMessage("Xin chào! Tôi là Bác sĩ Thú y AI của PetCare 🐾. Bạn có thể gửi câu hỏi tư vấn triệu chứng, dinh dưỡng, tiêm phòng hoặc đính kèm hình ảnh biểu hiện của thú cưng để bác sĩ xem giúp nhé!");
            welcome.setCreatedAt(LocalDateTime.now());
            chatMessageRepository.save(welcome);
            list.add(welcome);
        }
        return list;
    }

    @Transactional
    public List<ChatMessage> sendMessage(String sessionId, String text, String imageUrl) {
        // 1. Lưu tin nhắn của khách hàng
        ChatMessage customerMsg = new ChatMessage();
        customerMsg.setSessionId(sessionId);
        customerMsg.setSender("CUSTOMER");
        customerMsg.setMessage(text != null && !text.isBlank() ? text.trim() : (imageUrl != null ? "[Hình ảnh thú cưng]" : "..."));
        customerMsg.setImageUrl(imageUrl);
        customerMsg.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(customerMsg);

        // 2. Sinh câu trả lời: ưu tiên Gemini AI, tự động fallback sang Rule Engine y khoa nếu chưa cấu hình API key
        String responseText = null;
        try {
            responseText = geminiAiService.askGemini(customerMsg.getMessage(), imageUrl);
        } catch (Exception ignored) {}

        if (responseText == null || responseText.isBlank()) {
            responseText = generateVetResponse(customerMsg.getMessage(), imageUrl);
        }

        ChatMessage vetMsg = new ChatMessage();
        vetMsg.setSessionId(sessionId);
        vetMsg.setSender("VET");
        vetMsg.setMessage(responseText);
        vetMsg.setCreatedAt(LocalDateTime.now().plusSeconds(1));
        chatMessageRepository.save(vetMsg);

        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    private String generateVetResponse(String msg, String imageUrl) {
        String lower = msg.toLowerCase();

        if (imageUrl != null && !imageUrl.isBlank()) {
            return "Bác sĩ đã nhận được hình ảnh của thú cưng. Nếu bé có dấu hiệu viêm da, mắt đỏ, vết thương hoặc nổi mẩn, bạn nên tránh để bé cào gãi và giữ vùng da sạch sẽ. Để chẩn đoán chính xác hơn, bạn có thể bấm 'Đặt lịch khám' hoặc gọi hotline 0383.553.886 để bác sĩ khám trực tiếp nhé!";
        }

        if (lower.contains("tiêm") || lower.contains("vaccine") || lower.contains("vắc xin")) {
            return "Dạ, lịch tiêm phòng tiêu chuẩn cho cún/mèo con thường bắt đầu từ 6-8 tuần tuổi (mũi đa bệnh), sau đó nhắc lại cách 3-4 tuần và tiêm phòng dại. Bạn có thể đặt lịch hẹn để bác sĩ đến tiêm tận nhà hoặc ghé phòng khám PetCare nhé!";
        } else if (lower.contains("triệt sản") || lower.contains("thiến")) {
            return "Dạ triệt sản an toàn cho chó mèo nên được thực hiện khi bé từ 5-6 tháng tuổi trở lên và có sức khỏe ổn định. Trước khi phẫu thuật cần nhịn ăn 6-8 tiếng. PetCare có quy trình gây mê an toàn và chăm sóc hậu phẫu chu đáo ạ.";
        } else if (lower.contains("nôn") || lower.contains("ỉa") || lower.contains("tiêu chảy") || lower.contains("bỏ ăn") || lower.contains("sốt")) {
            return "⚠️ Dấu hiệu nôn/bỏ ăn/tiêu chảy ở thú cưng có thể cảnh báo rối loạn tiêu hóa hoặc bệnh truyền nhiễm (như Parvo, Care). Bạn nên theo dõi sát, tạm thời cho uống oresol bù nước và đưa bé đến phòng khám PetCare sớm nhất để được xét nghiệm và truyền dịch kịp thời.";
        } else if (lower.contains("giá") || lower.contains("chi phí") || lower.contains("bao nhiêu")) {
            return "Dạ chi phí khám lâm sàng tại PetCare niêm yết từ 100.000đ - 150.000đ. Các gói dịch vụ Spa, tiêm phòng hay điều trị đều có bảng giá công khai tại mục 'Bảng giá' trên website của PetCare ạ.";
        } else if (lower.contains("địa chỉ") || lower.contains("ở đâu") || lower.contains("chi nhánh")) {
            return "Hệ thống PetCare có cơ sở tại Hà Nội và Quảng Ninh (phục vụ cấp cứu 24/24). Bạn có thể vào mục 'Chi nhánh' trên thanh menu để xem bản đồ chỉ đường chi tiết nhất nhé!";
        } else if (lower.contains("cấp cứu") || lower.contains("gấp") || lower.contains("khẩn cấp")) {
            return "🚨 ĐỐI VỚI TRƯỜNG HỢP CẤP CỨU: Bạn vui lòng gọi ngay Hotline 24/7: 0383.553.886 (Hà Nội) hoặc 0569.219.268 (Quảng Ninh) để bác sĩ cấp cứu chuẩn bị thiết bị hỗ trợ ngay lập tức!";
        } else {
            return "Bác sĩ đã ghi nhận thông tin tư vấn của bạn. Đội ngũ y bác sĩ PetCare sẽ phản hồi chi tiết thêm cho bạn. Nếu cần hỗ trợ khẩn cấp, bạn đừng ngần ngại gọi Hotline 0383.553.886 nhé!";
        }
    }
}
