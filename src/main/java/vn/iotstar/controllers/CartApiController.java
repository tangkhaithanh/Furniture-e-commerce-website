package vn.iotstar.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Product;
import vn.iotstar.services.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    @Autowired
    private CartService cartService;
    
    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(HttpSession session) {
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            userId = 1L; // Giá trị mặc định
        }
        int count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/getCartItems")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("user0");
            if (userId == null) {
            }
            
            Cart cart = cartService.getCartByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            
            if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
                List<Map<String, Object>> items = new ArrayList<>();
                
                for (CartItem item : cart.getCartItems()) {
                    Product product = item.getProduct();
                    Map<String, Object> itemMap = new HashMap<>();
                    
                    // Thông tin sản phẩm
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", product.getId());
                    productMap.put("name", product.getName());
                    productMap.put("price", product.getPrice());
                    productMap.put("imageUrl", product.getImageUrl());
                    
                    // Thông tin cart item
                    itemMap.put("product", productMap);
                    itemMap.put("quantity", item.getQuantity());
                    items.add(itemMap);
                }
                
                response.put("cartItems", items);
            } else {
                response.put("cartItems", new ArrayList<>());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Có lỗi xảy ra khi lấy thông tin giỏ hàng");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}