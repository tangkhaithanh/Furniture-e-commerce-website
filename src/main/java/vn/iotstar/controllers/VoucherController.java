package vn.iotstar.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Coupon;
import vn.iotstar.entity.User;
import vn.iotstar.services.CouponService;
import vn.iotstar.services.UserService;

@RestController
@RequestMapping("/voucher")
public class VoucherController {
    
    @Autowired
    private CouponService couponService;
    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchVoucher(@RequestParam("code") String code
    		,HttpSession session) {
        Coupon voucher = couponService.findByCode(code);
        
        if (voucher != null) {
        	if (voucher.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mã giảm giá đã hết."));
            }
        	Long userId = (Long) session.getAttribute("user0");
            if (userId == null) {
                userId = 1L;
            }
            User user = userService.findById(userId);
        	boolean alreadyUsed = user.getUserCoupons().stream()
                    .anyMatch(uc -> uc.getCoupon().equals(voucher));

            if (alreadyUsed) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Bạn đã sử dụng mã giảm giá này."));
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("voucher", voucher);
                return ResponseEntity.ok(response);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}