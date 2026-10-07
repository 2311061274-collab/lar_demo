package com.petcare.auth;

import com.petcare.auth.AppUserRepository;
import com.petcare.customer.Customer;
import com.petcare.customer.CustomerRepository;
import com.petcare.mail.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(
            AppUserRepository appUserRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.appUserRepository = appUserRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            @RequestParam(value = "verifyEmail", required = false) String verifyEmail,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "verified", required = false) String verified,
            @RequestParam(value = "verifyError", required = false) String verifyError,
            @RequestParam(value = "resetSuccess", required = false) String resetSuccess,
            java.security.Principal principal,
            Model model
    ) {
        // NẾU ĐÃ ĐĂNG NHẬP: CHUYỂN HƯỚNG DASHBOARD RÕ RÀNG THEO QUYỀN
        if (principal != null) {
            AppUser currentUser = appUserRepository.findByUsername(principal.getName())
                    .or(() -> appUserRepository.findByPhone(principal.getName())).orElse(null);
            if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/customer/history";
        }

        if (error != null) {
            model.addAttribute("loginError", "Tên đăng nhập / số điện thoại hoặc mật khẩu không chính xác (hoặc tài khoản chưa được kích hoạt qua Gmail)!");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Bạn đã đăng xuất an toàn khỏi hệ thống PetCare.");
        }
        if (registered != null) {
            model.addAttribute("registeredMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập để tiếp tục.");
        }
        if (resetSuccess != null) {
            model.addAttribute("registeredMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới của bạn.");
        }
        if (verifyEmail != null) {
            model.addAttribute("verifyEmailMessage", "Đăng ký thành công! Hệ thống đã gửi mã xác nhận tới Gmail: " + (email != null ? email : "") + ". Vui lòng kiểm tra hộp thư!");
        }
        if (verified != null) {
            model.addAttribute("registeredMessage", "Xác nhận Gmail thành công! Tài khoản của bạn đã được kích hoạt. Hãy đăng nhập ngay!");
        }
        if (verifyError != null) {
            model.addAttribute("loginError", "Mã hoặc liên kết xác thực không hợp lệ hoặc đã hết hạn. Vui lòng thử lại!");
        }
        return "auth/login";
    }

    @GetMapping("/verify-code")
    public String showVerifyCodePage(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "auth/verify-code";
    }

    @PostMapping("/verify-code")
    public String handleVerifyCode(
            @RequestParam("code") String code,
            @RequestParam(value = "email", required = false) String email,
            Model model
    ) {
        String cleanCode = (code != null) ? code.trim() : "";
        String cleanEmail = (email != null) ? email.trim().toLowerCase() : "";
        model.addAttribute("email", cleanEmail);

        if (cleanCode.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập mã kích hoạt 6 chữ số!");
            return "auth/verify-code";
        }

        AppUser user = null;
        if (!cleanEmail.isBlank()) {
            user = appUserRepository.findByEmail(cleanEmail).orElse(null);
            if (user != null && !cleanCode.equals(user.getVerificationToken())) {
                user = null;
            }
        }
        if (user == null) {
            user = appUserRepository.findByVerificationToken(cleanCode).orElse(null);
        }

        if (user == null) {
            model.addAttribute("error", "Mã kích hoạt không chính xác hoặc không tồn tại. Vui lòng kiểm tra lại email!");
            return "auth/verify-code";
        }

        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã kích hoạt đã hết hạn (24 giờ). Vui lòng đăng ký lại tài khoản!");
            return "auth/verify-code";
        }

        user.setActive(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        appUserRepository.save(user);

        return "redirect:/login?verified=true";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam(value = "token", required = false) String token,
            Model model
    ) {
        if (token == null || token.isBlank()) {
            return "redirect:/login?verifyError=invalid";
        }

        AppUser user = appUserRepository.findByVerificationToken(token.trim()).orElse(null);
        if (user == null) {
            return "redirect:/login?verifyError=not_found";
        }

        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return "redirect:/login?verifyError=expired";
        }

        user.setActive(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        appUserRepository.save(user);

        return "redirect:/login?verified=true";
    }

    @PostMapping("/verify-email-code")
    public String verifyEmailCode(
            @RequestParam("code") String code
    ) {
        if (code == null || code.isBlank()) {
            return "redirect:/verify-code?error=invalid";
        }

        AppUser user = appUserRepository.findByVerificationToken(code.trim()).orElse(null);
        if (user == null) {
            return "redirect:/verify-code?error=not_found";
        }

        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return "redirect:/verify-code?error=expired";
        }

        user.setActive(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        appUserRepository.save(user);

        return "redirect:/login?verified=true";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("accountIdentifier") String identifier,
            Model model
    ) {
        if (identifier == null || identifier.trim().isBlank()) {
            model.addAttribute("error", "Vui lòng nhập Email, Tên đăng nhập hoặc Số điện thoại của bạn!");
            return "auth/forgot-password";
        }

        String clean = identifier.trim();
        AppUser user = appUserRepository.findByEmail(clean.toLowerCase())
                .or(() -> appUserRepository.findByUsername(clean.toLowerCase()))
                .or(() -> appUserRepository.findByPhone(clean.replaceAll("[^0-9]", "")))
                .orElse(null);

        if (user == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản nào khớp với thông tin đã nhập!");
            model.addAttribute("accountIdentifier", identifier);
            return "auth/forgot-password";
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            model.addAttribute("error", "Tài khoản của bạn chưa có email liên kết để nhận mã khôi phục. Vui lòng liên hệ hotline hỗ trợ!");
            model.addAttribute("accountIdentifier", identifier);
            return "auth/forgot-password";
        }

        String resetCode = String.format("%06d", (int) (Math.random() * 900000) + 100000);
        user.setVerificationToken(resetCode);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));
        appUserRepository.save(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName() != null ? user.getFullName() : user.getUsername(), resetCode);
        } catch (Exception e) {
            model.addAttribute("error", "Gặp sự cố khi gửi email xác nhận: " + e.getMessage());
            model.addAttribute("accountIdentifier", identifier);
            return "auth/forgot-password";
        }

        return "redirect:/reset-password?email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @RequestParam(value = "email", required = false) String email,
            Model model
    ) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("email") String email,
            @RequestParam("code") String code,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {
        String cleanEmail = (email != null) ? email.trim().toLowerCase() : "";
        String cleanCode = (code != null) ? code.trim() : "";
        model.addAttribute("email", cleanEmail);

        if (cleanEmail.isBlank() || cleanCode.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ Email và mã xác nhận 6 số từ hòm thư!");
            return "auth/reset-password";
        }

        AppUser user = appUserRepository.findByEmail(cleanEmail).orElse(null);
        if (user == null || !cleanCode.equals(user.getVerificationToken())) {
            model.addAttribute("error", "Mã xác nhận 6 số không đúng hoặc địa chỉ email không khớp!");
            return "auth/reset-password";
        }

        if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã xác nhận đã hết hạn (15 phút). Vui lòng yêu cầu cấp lại mã mới!");
            return "auth/reset-password";
        }

        // Quản lý mật khẩu chặt chẽ: ít nhất 8 ký tự, có cả chữ và số
        if (newPassword == null || newPassword.length() < 8 || !newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 8 ký tự và phải bao gồm cả chữ cái và chữ số!");
            return "auth/reset-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp với mật khẩu mới!");
            return "auth/reset-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        user.setActive(true);
        appUserRepository.save(user);

        return "redirect:/login?resetSuccess=true";
    }

    @GetMapping("/register")
    public String showRegisterForm(java.security.Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        String cleanUsername = username.trim().toLowerCase();

        // 1. KIỂM TRA HỌ TÊN
        if (fullName == null || fullName.trim().length() < 2) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ họ và tên của bạn!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        // 2. KIỂM TRA SỐ ĐIỆN THOẠI
        if (cleanPhone.length() != 10 || !cleanPhone.matches("^(03|05|07|08|09)[0-9]{8}$")) {
            model.addAttribute("error", "Số điện thoại không hợp lệ (phải gồm 10 số bắt đầu bằng 03, 05, 07, 08, 09)!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        AppUser existingUserByPhone = appUserRepository.findByPhone(cleanPhone).orElse(null);
        if (existingUserByPhone != null && Boolean.TRUE.equals(existingUserByPhone.getActive())) {
            model.addAttribute("error", "Số điện thoại " + cleanPhone + " đã được đăng ký và kích hoạt tài khoản. Vui lòng đăng nhập!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        // 3. KIỂM TRA TÊN ĐĂNG NHẬP CHẶT CHẼ
        if (cleanUsername.length() < 4 || cleanUsername.length() > 30 || !cleanUsername.matches("^[a-z0-9_]+$")) {
            model.addAttribute("error", "Tên đăng nhập phải từ 4 đến 30 ký tự, viết liền không dấu, chỉ chứa chữ cái thường, số và dấu gạch dưới (_)! Ví dụ: tuanh19");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        AppUser existingUserByUsername = appUserRepository.findByUsername(cleanUsername).orElse(null);
        if (existingUserByUsername != null && Boolean.TRUE.equals(existingUserByUsername.getActive())) {
            model.addAttribute("error", "Tên đăng nhập '" + cleanUsername + "' đã được sử dụng bởi tài khoản khác. Vui lòng chọn tên khác!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        // 4. KIỂM TRA MẬT KHẨU CHẶT CHẼ (TỐI THIỂU 8 KÝ TỰ, CÓ CẢ CHỮ VÀ SỐ)
        if (password.length() < 8 || !password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            model.addAttribute("error", "Mật khẩu phải có độ dài tối thiểu 8 ký tự và phải chứa cả chữ cái và chữ số để đảm bảo an toàn!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        // 5. KIỂM TRA GMAIL CHẶT CHẼ
        if (email == null || email.isBlank() || !email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            model.addAttribute("error", "Vui lòng nhập địa chỉ Gmail/Email hợp lệ (ví dụ: yourname@gmail.com) để nhận mã kích hoạt!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        String cleanEmail = email.trim().toLowerCase();

        AppUser existingUserByEmail = appUserRepository.findByEmail(cleanEmail).orElse(null);
        if (existingUserByEmail != null && Boolean.TRUE.equals(existingUserByEmail.getActive())) {
            model.addAttribute("error", "Địa chỉ Gmail '" + cleanEmail + "' đã được đăng ký và kích hoạt tài khoản. Vui lòng đăng nhập hoặc chọn Quên mật khẩu!");
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        // XỬ LÝ TRƯỜNG HỢP ĐÃ ĐĂNG KÝ NHƯNG CHƯA NHẬP MÃ XÁC NHẬN:
        // Tái sử dụng tài khoản chưa active của email này, hoặc dọn dẹp các tài khoản chưa active bị trùng
        AppUser userToSave = null;
        if (existingUserByEmail != null && !Boolean.TRUE.equals(existingUserByEmail.getActive())) {
            userToSave = existingUserByEmail;
        } else if (existingUserByUsername != null && !Boolean.TRUE.equals(existingUserByUsername.getActive())) {
            userToSave = existingUserByUsername;
        } else if (existingUserByPhone != null && !Boolean.TRUE.equals(existingUserByPhone.getActive())) {
            userToSave = existingUserByPhone;
        } else {
            userToSave = new AppUser();
        }

        // Dọn dẹp tài khoản chưa kích hoạt khác nếu trùng username hoặc phone để tránh vi phạm unique constraint
        if (existingUserByUsername != null && !Boolean.TRUE.equals(existingUserByUsername.getActive())
                && userToSave.getId() != null && !existingUserByUsername.getId().equals(userToSave.getId())) {
            appUserRepository.delete(existingUserByUsername);
            appUserRepository.flush();
        }
        if (existingUserByPhone != null && !Boolean.TRUE.equals(existingUserByPhone.getActive())
                && userToSave.getId() != null && !existingUserByPhone.getId().equals(userToSave.getId())) {
            appUserRepository.delete(existingUserByPhone);
            appUserRepository.flush();
        }

        // Link with existing customer or create new
        Customer customer = customerRepository.findFirstByPhone(cleanPhone).orElseGet(Customer::new);
        customer.setFullName(fullName.trim());
        customer.setPhone(cleanPhone);
        customer.setEmail(cleanEmail);
        customer = customerRepository.save(customer);

        String verificationToken = String.format("%06d", (int) (Math.random() * 900000) + 100000);

        userToSave.setUsername(cleanUsername);
        userToSave.setPassword(passwordEncoder.encode(password));
        userToSave.setRole("CUSTOMER");
        userToSave.setActive(false); // Vẫn chưa kích hoạt cho đến khi nhập mã
        userToSave.setEmail(cleanEmail);
        userToSave.setVerificationToken(verificationToken);
        userToSave.setTokenExpiry(LocalDateTime.now().plusHours(24));
        userToSave.setCustomer(customer);
        userToSave.setPhone(cleanPhone);
        userToSave.setFullName(fullName.trim());

        appUserRepository.save(userToSave);

        // Gửi email xác nhận qua Gmail với mã 6 số mới
        try {
            emailService.sendVerificationEmail(cleanEmail, fullName.trim(), verificationToken);
        } catch (Exception e) {
            model.addAttribute("error", "Đã ghi nhận đăng ký nhưng tạm thời gặp trục trặc khi gửi mã qua Gmail: " + e.getMessage());
            fillRegisterBack(model, fullName, phone, email, username);
            return "auth/register";
        }

        return "redirect:/verify-code?email=" + URLEncoder.encode(cleanEmail, StandardCharsets.UTF_8);
    }

    private void fillRegisterBack(Model model, String fullName, String phone, String email, String username) {
        model.addAttribute("fullName", fullName);
        model.addAttribute("phone", phone);
        model.addAttribute("email", email);
        model.addAttribute("username", username);
    }
}
