package com.petcare.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GeminiAiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-2.5-flash}")
    private String model;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String askGemini(String userMessage, String dataUrl) {
        if (!isConfigured()) {
            return null;
        }

        try {
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + model.trim() + ":generateContent?key=" + apiKey.trim();

            String systemPrompt = "Bạn là Bác sĩ Thú y AI của Bệnh viện Thú Y Uy Tín PetCare (trụ sở chính 8 Khuất Duy Tiến, Thanh Xuân, Hà Nội & cơ sở Quảng Ninh). "
                    + "Bạn có chuyên môn sâu về chẩn đoán, dinh dưỡng, tiêm phòng vaccine và chăm sóc chó mèo, thú cưng. "
                    + "Hãy trả lời ân cần, chuẩn mực y khoa, súc tích (khoảng 2-4 câu), đưa ra lời khuyên hữu ích và hướng dẫn khách hàng liên hệ hotline 0383.553.886 hoặc bấm 'Đặt lịch khám' tại PetCare khi cần thiết.";

            StringBuilder partsJson = new StringBuilder();
            partsJson.append("{\"text\": ").append(escapeJson(userMessage != null && !userMessage.isBlank() ? userMessage : "Nhờ bác sĩ xem hình ảnh thú cưng này và cho lời khuyên.")).append("}");

            if (dataUrl != null && dataUrl.startsWith("data:")) {
                int commaIndex = dataUrl.indexOf(',');
                if (commaIndex != -1) {
                    String header = dataUrl.substring(5, commaIndex);
                    String base64Data = dataUrl.substring(commaIndex + 1);
                    String mimeType = "image/jpeg";
                    if (header.contains(";")) {
                        mimeType = header.substring(0, header.indexOf(';'));
                    }
                    partsJson.append(", {\"inline_data\": {\"mime_type\": \"").append(mimeType)
                            .append("\", \"data\": \"").append(base64Data).append("\"}}");
                }
            }

            String requestBody = "{"
                    + "\"system_instruction\": {\"parts\": [{\"text\": " + escapeJson(systemPrompt) + "}]},"
                    + "\"contents\": [{\"role\": \"user\", \"parts\": [" + partsJson + "]}]"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(25))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractTextFromResponse(response.body());
            } else {
                log.warn("Gemini API call failed with status: {}, body: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("Exception when calling Gemini API: {}", e.getMessage());
            return null;
        }
    }

    private String extractTextFromResponse(String json) {
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"((?:\\\\\"|[^\"])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String rawText = matcher.group(1);
            return unescapeJson(rawText);
        }
        return null;
    }

    private String escapeJson(String str) {
        if (str == null) return "\"\"";
        return "\"" + str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    private String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
