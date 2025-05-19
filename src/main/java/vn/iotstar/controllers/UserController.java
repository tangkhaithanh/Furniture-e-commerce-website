package vn.iotstar.controllers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vn.iotstar.dto.OtpDTO;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.dto.UserLoginDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.exceptions.CustomerNotFoundException;
import vn.iotstar.services.UserService;
import vn.iotstar.services.impl.EmailService;
import vn.iotstar.services.impl.OTPService;
import vn.iotstar.utils.URLUtil;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO,
                                        BindingResult result, HttpSession session) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                // Trả về thông báo lỗi dưới dạng JSON
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", String.join(", ", errorMessages)));
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                // Trả về thông báo lỗi mật khẩu không khớp
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Password not match"));
            }
            String otp = otpService.generateOTP();
            // Gửi OTP qua email
            emailService.sendOTPEmail(userDTO.getEmail(), otp);
            // Lưu OTP vào session (hoặc lưu vào cơ sở dữ liệu với thời gian hết hạn)
            session.setAttribute("otp", otp);
            session.setAttribute("email", userDTO.getEmail());
            session.setAttribute("userDTO", userDTO);
//            User user = userService.createUser(userDTO);
            // Trả về thông báo thành công dưới dạng JSON
            return ResponseEntity.ok(Collections.singletonMap("message", "Send mail successfully"));
//            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            // Trả về thông báo lỗi trong trường hợp có ngoại lệ
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", ex.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody OtpDTO otp, HttpSession session) throws Exception {
        String sessionOtp = (String) session.getAttribute("otp");
        String sessionEmail = (String) session.getAttribute("email");
        UserDTO userDTO = (UserDTO) session.getAttribute("userDTO");

        if (sessionOtp != null && sessionOtp.equals(otp.getOTP())) {
            // Bước 7: Đăng ký người dùng nếu OTP hợp lệ
            User user = userService.createUser(userDTO);
            session.removeAttribute("otp");  // Xóa OTP sau khi xác thực thành công
            return ResponseEntity.ok(Collections.singletonMap("message", "Send mail successfully"));
        }
        return null;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserLoginDTO());
        return "login";
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            String token = userService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            HttpSession session = getCurrentSession();
            Optional<User> user = getUser(userLoginDTO.getUsername());
            Long userId = user.get().getUserId();
            String userName=user.get().getUsername();
            Role role = user.get().getRole();
            session.setAttribute("user0", userId);
            session.setAttribute("userName", userName);
            // Trả về token dưới dạng JSON
            return ResponseEntity.ok(new LoginResponse(token, role.getName()));
        } catch (Exception e) {
            // Trả về lỗi dưới dạng JSON
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    private HttpSession getCurrentSession() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
    }

    // Giả sử bạn có một phương thức lấy userId từ username
    private Optional<User> getUser(String username) {
        return userService.getUserByUsername(username);
    }

    public class LoginResponse {
        private String token;
        private String role;

        public LoginResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        // Getters and setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }


    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @GetMapping("/forgot-password")
    public String forgot(Model model) {
        return "forgot";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(45);
        try {
            userService.updateResetPasswordToken(token, email);

            String resetPasswordLink = URLUtil.getSiteUrl(request) + "/users/reset-password?token=" + token;

            sendEmail(email, resetPasswordLink);

            model.addAttribute("message", "We have sent a reset password link to your email. Please check email!");

        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (UnsupportedEncodingException | MessagingException ex) {
            model.addAttribute("error", "Error while sending mail");
        }
        return "forgot";
    }

    private void sendEmail(String email, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage);
        mimeMessageHelper.setFrom("bumngo364@gmail.com", "Home Nest");
        mimeMessageHelper.setTo(email);
        String subject = "Here is the link to reset your password ";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + resetPasswordLink + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        mimeMessageHelper.setSubject(subject);

        mimeMessageHelper.setText(content, true);

        mailSender.send(mimeMailMessage);
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getUser(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "Invalid Token");
            return "message";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = userService.getUser(token);
        if (user == null) {
            model.addAttribute("message", "Invalid Token");
            return "message";
        } else {
            userService.updatePassword(user, password);
            model.addAttribute("message", "You have successfully changed your password.");
        }
        return "message";
    }

    @PostMapping("/logout")
    public String logout() {
        // Đặt lại Authentication của người dùng khi logout
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Đảm bảo rằng người dùng đã logout khỏi hệ thống
            SecurityContextHolder.clearContext();
        }
        return "redirect:/users/login"; // Redirect về trang chủ sau khi logout
    }

}
