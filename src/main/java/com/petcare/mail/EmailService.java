package com.petcare.mail;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String fullName, String token) {
        String confirmationUrl = baseUrl + "/verify-email?token=" + token;

        String htmlContent = """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #F8FAFC; margin: 0; padding: 20px; }
                    .email-card { max-width: 580px; margin: 0 auto; background: #FFFFFF; border-radius: 16px; border: 1px solid #E2E8F0; overflow: hidden; box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
                    .email-header { background: linear-gradient(135deg, #0284C7 0%, #0369A1 100%); padding: 32px 24px; text-align: center; color: white; }
                    .email-header h1 { margin: 0; font-size: 24px; font-weight: 800; }
                    .email-header p { margin: 6px 0 0 0; font-size: 14px; opacity: 0.9; }
                    .email-body { padding: 32px 28px; color: #334155; line-height: 1.6; }
                    .greeting { font-size: 16px; font-weight: 700; color: #0F172A; margin-bottom: 12px; }
                    .btn-wrapper { text-align: center; margin: 32px 0; }
                    .btn-verify { background: #0284C7; color: #FFFFFF !important; text-decoration: none; padding: 14px 32px; border-radius: 10px; font-weight: 700; font-size: 15px; display: inline-block; box-shadow: 0 4px 12px rgba(2, 132, 199, 0.35); }
                    .link-alt { background: #F1F5F9; border-radius: 8px; padding: 12px; font-size: 12px; word-break: break-all; color: #64748B; margin-top: 20px; }
                    .email-footer { background: #F8FAFC; border-top: 1px solid #F1F5F9; padding: 20px; text-align: center; font-size: 12.5px; color: #94A3B8; }
                </style>
            </head>
            <body>
                <div class="email-card">
                    <div class="email-header">
                        <h1>🐾 PetCare Clinic</h1>
                        <p>Bệnh Viện Thú Y & Chăm Sóc Thú Cưng Hàng Đầu</p>
                    </div>
                    <div class="email-body">
                        <div class="greeting">Xin chào {{fullName}},</div>
                        <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>PetCare Clinic</strong>. Để hoàn tất kích hoạt tài khoản và bảo mật thông tin hồ sơ thú cưng, bạn có thể thực hiện 1 trong 2 cách sau:</p>
                        
                        <!-- CÁCH 1: MÃ KÍCH HOẠT 6 SỐ (TIỆN LỢI KHI MỞ MAIL TRÊN ĐIỆN THOẠI) -->
                        <div style="background: #F0FDF4; border: 2px dashed #16A34A; border-radius: 12px; padding: 18px; text-align: center; margin: 20px 0;">
                            <div style="font-size: 12px; font-weight: 800; color: #15803D; text-transform: uppercase; letter-spacing: 1px;">🔑 MÃ KÍCH HOẠT TÀI KHOẢN CỦA BẠN</div>
                            <div style="font-size: 34px; font-weight: 900; color: #166534; letter-spacing: 8px; margin: 10px 0; font-family: monospace;">{{otpCode}}</div>
                            <div style="font-size: 12px; color: #4B5563;">👉 Nhập mã 6 số này tại màn hình Đăng nhập PetCare trên máy tính của bạn</div>
                        </div>

                        <!-- CÁCH 2: BẤM NÚT NẾU ĐANG DÙNG TRÌNH DUYỆT TRÊN MÁY TÍNH -->
                        <div class="btn-wrapper" style="margin: 20px 0;">
                            <a href="{{confirmationUrl}}" class="btn-verify" target="_blank">Hoặc Bấm Kích Hoạt Trực Tiếp</a>
                        </div>

                        <p style="font-size: 13px; color: #64748B;">
                            ⏰ <em>Mã kích hoạt và liên kết có hiệu lực trong vòng <strong>24 giờ</strong>.</em>
                        </p>
                        <p style="font-size: 12.5px; color: #64748B;">
                            Nếu nút trên không hoạt động, bạn có thể dán liên kết này vào trình duyệt máy tính:
                        </p>
                        <div class="link-alt">{{confirmationUrl}}</div>
                    </div>
                    <div class="email-footer">
                        <p style="margin: 0 0 4px 0;">Hotline Cấp Cứu 24/7: <strong>0383.553.886</strong></p>
                        <p style="margin: 0;">Nếu bạn không thực hiện đăng ký này, xin vui lòng bỏ qua email này.</p>
                    </div>
                </div>
            </body>
            </html>
            """
            .replace("{{fullName}}", fullName != null ? fullName : "Quý khách")
            .replace("{{otpCode}}", token)
            .replace("{{confirmationUrl}}", confirmationUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "Bệnh Viện Thú Y PetCare");
            helper.setTo(toEmail);
            helper.setSubject("🐾 [PetCare] Xác nhận đăng ký tài khoản của bạn");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email verification sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Lỗi gửi email xác thực qua Gmail: " + e.getMessage(), e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String fullName, String resetCode) {
        String htmlContent = """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #F8FAFC; margin: 0; padding: 20px; }
                    .email-card { max-width: 580px; margin: 0 auto; background: #FFFFFF; border-radius: 16px; border: 1px solid #E2E8F0; overflow: hidden; box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
                    .email-header { background: linear-gradient(135deg, #0284C7 0%, #0369A1 100%); padding: 32px 24px; text-align: center; color: white; }
                    .email-header h1 { margin: 0; font-size: 24px; font-weight: 800; }
                    .email-header p { margin: 6px 0 0 0; font-size: 14px; opacity: 0.9; }
                    .email-body { padding: 32px 28px; color: #334155; line-height: 1.6; }
                    .greeting { font-size: 16px; font-weight: 700; color: #0F172A; margin-bottom: 12px; }
                    .email-footer { background: #F8FAFC; border-top: 1px solid #F1F5F9; padding: 20px; text-align: center; font-size: 12.5px; color: #94A3B8; }
                </style>
            </head>
            <body>
                <div class="email-card">
                    <div class="email-header">
                        <h1>🔐 PetCare Clinic</h1>
                        <p>Yêu Cầu Đặt Lại Mật Khẩu Tài Khoản</p>
                    </div>
                    <div class="email-body">
                        <div class="greeting">Xin chào {{fullName}},</div>
                        <p>Hệ thống nhận được yêu cầu đặt lại mật khẩu cho tài khoản PetCare của bạn. Dưới đây là mã xác nhận OTP dùng để tạo mật khẩu mới:</p>
                        
                        <div style="background: #FEF3C7; border: 2px dashed #F59E0B; border-radius: 12px; padding: 20px; text-align: center; margin: 24px 0;">
                            <div style="font-size: 12px; font-weight: 800; color: #B45309; text-transform: uppercase; letter-spacing: 1px;">🔑 MÃ XÁC NHẬN ĐẶT LẠI MẬT KHẨU</div>
                            <div style="font-size: 36px; font-weight: 900; color: #92400E; letter-spacing: 8px; margin: 10px 0; font-family: monospace;">{{resetCode}}</div>
                            <div style="font-size: 12.5px; color: #78350F;">Mã xác nhận có hiệu lực trong vòng <strong>15 phút</strong>.</div>
                        </div>

                        <p style="font-size: 13.5px; color: #64748B;">
                            Vui lòng nhập mã này trên trang web PetCare và thiết lập mật khẩu mới (tối thiểu 8 ký tự, gồm cả chữ và số).
                        </p>
                        <p style="font-size: 13px; color: #EF4444;">
                            ⚠️ Nếu bạn không gửi yêu cầu này, vui lòng bỏ qua email. Mật khẩu hiện tại của bạn vẫn được bảo vệ an toàn.
                        </p>
                    </div>
                    <div class="email-footer">
                        <p style="margin: 0 0 4px 0;">Hotline Hỗ Trợ 24/7: <strong>0383.553.886</strong></p>
                        <p style="margin: 0;">Bệnh viện thú y PetCare - Đồng hành chăm sóc thú cưng</p>
                    </div>
                </div>
            </body>
            </html>
            """
            .replace("{{fullName}}", fullName != null ? fullName : "Quý khách")
            .replace("{{resetCode}}", resetCode);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "Bệnh Viện Thú Y PetCare");
            helper.setTo(toEmail);
            helper.setSubject("🔐 [PetCare] Mã xác nhận đặt lại mật khẩu của bạn");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Lỗi gửi email đặt lại mật khẩu qua Gmail: " + e.getMessage(), e);
        }
    }
}
