package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.User;
import vn.iotstar.services.AddressService;
import vn.iotstar.services.UserService;


@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;

    // Hiển thị form thêm hoặc cập nhật địa chỉ
    @PostMapping("/save")
    public String saveAddress(@ModelAttribute Address address, @RequestParam String orderId, @RequestParam List<String> selectedItems, @RequestParam boolean isDefault, HttpSession session, Model model
    		,RedirectAttributes redirectAttributes) {
    	Logger logger = LoggerFactory.getLogger(this.getClass());
    	Long orderIdLong;
        try {
            orderIdLong = Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            // Xử lý khi không parse được
            orderIdLong = null; // hoặc giá trị mặc định
        }
        logger.info("Is Default: {}", orderIdLong);
        
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            return "redirect:/users/login"; // Nếu không có userId trong session, giả sử là userId = 1
        }

        User user = userService.findById(userId);
        if (user != null) {
            // Nếu isDefault là true, cần cập nhật các địa chỉ khác của người dùng
            if (isDefault) {
                // Lấy tất cả địa chỉ của user, kiểm tra và cập nhật
                List<Address> userAddresses = addressService.getAddressesByUserId(userId);
                for (Address existingAddress : userAddresses) {
                    // Nếu địa chỉ đã là mặc định, set lại thành false
                    if (existingAddress.isDefault()) {
                        existingAddress.setDefault(false);
                        addressService.saveAddress(existingAddress);  // Lưu lại địa chỉ đã cập nhật
                    }
                }
            }

            // Cập nhật địa chỉ hiện tại, đảm bảo isDefault được xử lý đúng
            address.setUser(user);
            address.setDefault(isDefault);  // Đặt địa chỉ này là mặc định nếu isDefault = true
            addressService.saveAddress(address);  // Lưu địa chỉ mới hoặc cập nhật

            model.addAttribute("message", "Cập nhật địa chỉ thành công!");
        }
		/* logger.info("Is Default: {}", isDefault); */
        // Điều hướng đến trang thông tin đơn hàng với orderId
        return "redirect:/order/summary/" + orderIdLong + "?selectedItems=" + String.join(",", selectedItems);
    }
    
 // Thêm phương thức GET cho URL /address/info
    @GetMapping("/info")
    public String getAddressInfo(HttpSession session, Model model) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            return "redirect:/users/login";  // Nếu không có userId trong session, giả sử là userId = 1
        }

        // Lấy thông tin người dùng
        User user = userService.findById(userId);
        if (user != null) {
            // Lấy tất cả địa chỉ của user
            List<Address> userAddresses = addressService.getAddressesByUserId(userId);
            model.addAttribute("user", user);
            model.addAttribute("addresses", userAddresses);
        } else {
            model.addAttribute("message", "Không tìm thấy người dùng.");
        }

        // Trả về view "address_info" để hiển thị thông tin địa chỉ
        return "address_info"; // Đây là tên của trang HTML (template) sẽ hiển thị thông tin
    }
    
    @PostMapping("/saves")
    public String saveAddress1(@ModelAttribute Address address, @RequestParam boolean isDefault, HttpSession session, Model model
    		,RedirectAttributes redirectAttributes) {
    	Logger logger = LoggerFactory.getLogger(this.getClass());
        
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            return "redirect:/users/login"; // Nếu không có userId trong session, giả sử là userId = 1
        }

        User user = userService.findById(userId);
        if (user != null) {
            // Nếu isDefault là true, cần cập nhật các địa chỉ khác của người dùng
            if (isDefault) {
                // Lấy tất cả địa chỉ của user, kiểm tra và cập nhật
                List<Address> userAddresses = addressService.getAddressesByUserId(userId);
                for (Address existingAddress : userAddresses) {
                    // Nếu địa chỉ đã là mặc định, set lại thành false
                    if (existingAddress.isDefault()) {
                        existingAddress.setDefault(false);
                        addressService.saveAddress(existingAddress);  // Lưu lại địa chỉ đã cập nhật
                    }
                }
            }

            // Cập nhật địa chỉ hiện tại, đảm bảo isDefault được xử lý đúng
            address.setUser(user);
            address.setDefault(isDefault);  // Đặt địa chỉ này là mặc định nếu isDefault = true
            addressService.saveAddress(address);  // Lưu địa chỉ mới hoặc cập nhật

            model.addAttribute("message", "Cập nhật địa chỉ thành công!");
        }
		/* logger.info("Is Default: {}", isDefault); */
        // Điều hướng đến trang thông tin đơn hàng với orderId
        return "redirect:/address/info";
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, HttpSession session) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Deleting address with id: {}", id);
        try {
            // Lấy userId từ session
            Long userId = (Long) session.getAttribute("user0");
            if (userId == null) {
                userId = 1L; // Giá trị mặc định nếu không có session
            }

            // Tìm địa chỉ cần xóa
            Address address = addressService.getAddressById(id);
            
            // Kiểm tra xem địa chỉ có tồn tại không
            if (address == null) {
                logger.error("Address not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            // Kiểm tra xem địa chỉ có thuộc về user hiện tại không
            if (!address.getUser().getUserId().equals(userId)) {
                logger.error("Unauthorized deletion attempt for address id: {}", id);
                return ResponseEntity.status(403).body("Không có quyền xóa địa chỉ này");
            }

            // Nếu là địa chỉ mặc định, không cho phép xóa
            if (address.isDefault()) {
                logger.warn("Attempt to delete default address id: {}", id);
                return ResponseEntity.badRequest().body("Không thể xóa địa chỉ mặc định");
            }

            // Thực hiện xóa địa chỉ
            addressService.deleteAddress(id);
            logger.info("Successfully deleted address with id: {}", id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa địa chỉ thành công");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error deleting address with id: " + id, e);
            return ResponseEntity.status(500).body("Có lỗi xảy ra khi xóa địa chỉ");
        }
    }
}
