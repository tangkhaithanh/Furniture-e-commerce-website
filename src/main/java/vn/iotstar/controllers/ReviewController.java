package vn.iotstar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.services.CategoryService;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.ProductService;
import vn.iotstar.services.ReviewService;

@Controller
@RequestMapping("/order")
public class ReviewController {
	@Autowired
    private ProductService productService;
	@Autowired
    private ReviewService reviewService;
	@GetMapping("/review/{productId}")
	public String showReviewForm(@PathVariable("productId") Long productId, Model model, HttpSession session) {
	    Long userId = (Long) session.getAttribute("user0");
	    if (userId == null) {
	        userId = 1L; // Giá trị mặc định nếu userId không có trong session
	    }

	    Product product = productService.getProductById(productId);
	    Review existingReview = reviewService.findByUserIdAndProductId(userId, productId);

	    model.addAttribute("product", product);
	    model.addAttribute("existingReview", existingReview);

	    return "reviewPage"; // Tên của view hiển thị form đánh giá
	}
	/*
	 * @PostMapping("/review/{orderId}") public String submitReview(@PathVariable
	 * Long orderId, @RequestParam int rating, @RequestParam String comment) { // Xử
	 * lý đánh giá và lưu vào cơ sở dữ liệu // Cập nhật trạng thái hoặc thông tin
	 * liên quan đến đánh giá return "redirect:/order/thank-you"; // Điều hướng đến
	 * trang cảm ơn sau khi gửi đánh giá }
	 */
}