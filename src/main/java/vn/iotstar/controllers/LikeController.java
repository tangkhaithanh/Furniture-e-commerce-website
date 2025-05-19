package vn.iotstar.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductLike;
import vn.iotstar.entity.User;
import vn.iotstar.repository.ProductLikeRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.ProductService;

@RestController
@RequestMapping("/like")
public class LikeController {
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private ProductService productService;
	@Autowired
    private ProductLikeRepository productLikeRepository;
	
    @PostMapping("/product")
    public String likeProduct(@RequestParam("productId") Long productId, HttpSession session) {
        // Lấy user có id = 1 từ database
        User user = userRepository.findByUserId((Long) session.getAttribute("user0")); // Tìm user có id = 1
        
        if (user == null) {
            // Nếu không tìm thấy user, có thể hiển thị lỗi hoặc thông báo gì đó
            return "redirect_to_login";
        }

        Product product = productService.getProductById(productId);

        if (product == null) {
            // Nếu không tìm thấy sản phẩm, có thể hiển thị lỗi
            return "error";
        }

        // Kiểm tra xem người dùng đã like sản phẩm này chưa
        ProductLike existingLike = productLikeRepository.findByProductAndUser(product, user);

        if (existingLike == null) {
            // Nếu chưa like, thêm like mới
            ProductLike newLike = new ProductLike();
            newLike.setProduct(product);
            newLike.setUser(user);
            productLikeRepository.save(newLike);
        } else {
            // Nếu đã like, xóa like
            productLikeRepository.delete(existingLike);
        }

        // Lấy lại thông tin sản phẩm sau khi cập nhật lượt thích
        int totalLikes = product.getProductLikes().size();

        // Trả về số lượt thích mới (JSON)
        return String.valueOf(totalLikes);
    }
}
