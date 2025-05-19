package vn.iotstar.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.iotstar.entity.Order;
import vn.iotstar.services.OrderService;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService; // Service xử lý đơn hàng

    @GetMapping("/qr")
    public String showPaymentQR(@RequestParam Long orderId, 
                              @RequestParam BigDecimal amount, 
                              Model model) {
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "payment_qr";
    }

    @GetMapping("/check-status")
    @ResponseBody
    public Map<String, String> checkPaymentStatus(@RequestParam Long orderId) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Giả lập việc kiểm tra thanh toán - trong thực tế sẽ tích hợp với API của cổng thanh toán
            Order order = orderService.getOrderById(orderId);
            
            // Ví dụ: cập nhật trạng thái khi nhận được callback từ cổng thanh toán
            if (order != null) {
                order.setStatus("Đã xác nhận");
                orderService.save(order);
                response.put("status", "completed");
            } else {
                response.put("status", "pending");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
}