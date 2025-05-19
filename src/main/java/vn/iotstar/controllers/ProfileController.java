package vn.iotstar.controllers;

import java.io.File;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.*;
import vn.iotstar.entity.User.Gender;
import vn.iotstar.services.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;
    @Value("${product.image.upload.dir}")
    private String uploadDir;

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
    	
    	Long userId = (Long) session.getAttribute("user0");
        
        User user=userService.findById(userId);
        
        // Đưa thông tin user vào model
        model.addAttribute("user", user);
        
        return "profile"; // Trả về view profile.html
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "gender", required = false) Gender gender,
            @RequestParam(value = "dateOfBirth", required = false) LocalDate dateOfBirth,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = (Long) session.getAttribute("user0");
        try {
            // Lấy user hiện tại
            User currentUser = userService.findById(userId);

            // Cập nhật từng trường
            if (email != null && !email.isEmpty()) {
                currentUser.setEmail(email);
            }

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                currentUser.setPhoneNumber(phoneNumber);
            }

            if (gender != null) {
                currentUser.setGender(gender);
            }

            if (dateOfBirth != null) {
                currentUser.setDateOfBirth(dateOfBirth);
            }

            // Xử lý upload avatar
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Tạo thư mục nếu chưa tồn tại
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs();
                }

                // Tạo tên file duy nhất
                String uniqueFileName = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDirectory.getPath(), uniqueFileName);
                
                // Kiểm tra định dạng ảnh
                String fileExtension = uniqueFileName.substring(uniqueFileName.lastIndexOf(".") + 1).toLowerCase();
                if (Arrays.asList("jpg", "jpeg", "png", "gif").contains(fileExtension)) {
                    // Lưu file
                    avatarFile.transferTo(filePath.toFile());
                    
                    // Lưu đường dẫn ảnh
                    String avatarUrl = "/images/" + uniqueFileName;
                    currentUser.setAvatarUrl(avatarUrl);
                } else {
                    // Báo lỗi nếu không phải file ảnh
                    redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file ảnh hợp lệ (jpg, jpeg, png, gif)");
                    return "redirect:/profile";
                }
            }

            // Lưu thông tin
            userService.saveUser(currentUser);

            // Thông báo cập nhật thành công
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công");

            return "redirect:/profile";
        } catch (Exception e) {
            // Xử lý lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}
