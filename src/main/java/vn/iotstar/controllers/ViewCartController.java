package vn.iotstar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.services.CartService;
import vn.iotstar.services.ProductService;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.Product;

@Controller
@RequestMapping("/cart")
public class ViewCartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

 // Xem giỏ hàng
    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
        	return "redirect:/users/login";
        }
        List<Product> bestSellingProducts = productService.getBestSellingProducts();

        // Lấy giỏ hàng của user
        var cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        model.addAttribute("bestSellingProducts", bestSellingProducts);

        // Chuyển tới trang giỏ hàng
        return "cart";  // Giả sử bạn có một trang cart.html trong thư mục templates
    }
    
    @GetMapping("/increaseQuantity")
    public String increaseQuantity(@RequestParam Long cartItemId) {
        // Tăng số lượng sản phẩm trong giỏ hàng
        cartService.changeQuantity(cartItemId, 1); // Thêm 1 vào số lượng
        return "redirect:/cart/view";
    }

    @GetMapping("/decreaseQuantity")
    public String decreaseQuantity(@RequestParam Long cartItemId) {
        // Giảm số lượng sản phẩm trong giỏ hàng
        cartService.changeQuantity(cartItemId, -1); // Giảm 1 vào số lượng
        return "redirect:/cart/view";
    }
    
 // Xử lý xóa sản phẩm khỏi giỏ hàng
    @GetMapping("/delete")
    public String deleteCartItem(@RequestParam("cartItemId") Long cartItemId) {
        try {
            cartService.removeItemFromCart(cartItemId);  // Gọi service để xóa sản phẩm khỏi giỏ hàng
            return "redirect:/cart/view";  // Sau khi xóa, chuyển hướng lại trang giỏ hàng
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return "error";  // Chuyển hướng tới trang lỗi nếu có lỗi
        }
    }

    
}
