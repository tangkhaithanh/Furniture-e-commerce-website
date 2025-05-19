package vn.iotstar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ContactController {

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        // Bạn có thể thêm thông tin cửa hàng vào model nếu cần
        ContactInfo contactInfo = new ContactInfo(
            "97 Man Thiện, Hiệp Phú, Thủ Đức, TP.HCM",
            "0123 456 789",
            "support@furnitureshop.com",
            "8:00 - 21:00",
            "9:00 - 20:00",
            "9:00 - 18:00"
        );
        model.addAttribute("contactInfo", contactInfo);
        
        // Thêm social media links nếu cần
        SocialMedia socialMedia = new SocialMedia(
            "https://facebook.com/yourshop",
            "https://twitter.com/yourshop",
            "https://instagram.com/yourshop",
            "https://linkedin.com/company/yourshop"
        );
        model.addAttribute("socialMedia", socialMedia);

        return "contact";
    }
}

// Class để lưu thông tin liên hệ
class ContactInfo {
    private String address;
    private String phone;
    private String email;
    private String weekdayHours;
    private String saturdayHours;
    private String sundayHours;

    public ContactInfo(String address, String phone, String email, 
                      String weekdayHours, String saturdayHours, String sundayHours) {
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.weekdayHours = weekdayHours;
        this.saturdayHours = saturdayHours;
        this.sundayHours = sundayHours;
    }

    // Getters and setters
    // ... thêm các getter và setter cho các trường
}

// Class để lưu thông tin mạng xã hội
class SocialMedia {
    private String facebook;
    private String twitter;
    private String instagram;
    private String linkedin;

    public SocialMedia(String facebook, String twitter, String instagram, String linkedin) {
        this.facebook = facebook;
        this.twitter = twitter;
        this.instagram = instagram;
        this.linkedin = linkedin;
    }

    // Getters and setters
    // ... thêm các getter và setter cho các trường
}