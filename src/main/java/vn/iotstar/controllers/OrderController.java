package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.*;
import vn.iotstar.repository.ReturnRequestRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.*;
import vn.iotstar.utils.ApiResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Value("${product.image.upload.dir}")
    private String uploadDir;

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ReturnRequestRepository returnRequestRepository;
    
    @Value("${product.image.upload.dir}")
    private String paymentUploadDir;

    // Tạo đơn hàng từ giỏ hàng
    @PostMapping("/create")
    public ResponseEntity<Long> createOrder(@RequestParam("selectedItems") List<Long> selectedItems, HttpSession session) {
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            userId = 1L;
        }

        User user = userService.findById(userId);
        List<CartItem> cartItems = getCartItemsByIds(selectedItems, userId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Tạo đơn hàng
        Order order = orderService.createOrder(user, cartItems);

        // Trả về ID của đơn hàng vừa tạo
        return ResponseEntity.ok(order.getId());
    }


    // Lấy các CartItem từ giỏ hàng dựa trên ids đã chọn
    private List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId) {
        // Cần viết logic để lấy các CartItem theo ids và userId từ cơ sở dữ liệu
        return orderService.getCartItemsByIds(selectedItems, userId);
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, Object> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("user0");
        final Logger logger = LoggerFactory.getLogger(OrderController.class);
        Long orderId = Long.valueOf(request.get("orderId").toString());
        String status = request.get("status").toString();

        Date currentDate = new Date();

        User user = userService.findById(userId);
        Order order = orderService.getOrderById(orderId);
        String username = user.getUsername();
        // Tạo đối tượng Notification
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setOrder(order);
        notification.setTimestamp(currentDate);

        notification.setRead(false);
        if (status.equals("Đã nhận hàng")) {
            notification.setMessage(username + " đã nhận được đơn hàng");
            notification.setStatus("đã nhận hàng");
            notificationService.save(notification);
        } else if (status.equals("Đang duyệt")){
            notification.setMessage("Yêu cầu trả hàng từ người dùng " + username);
            notification.setStatus("trả hàng");
            notificationService.save(notification);
        }
        logger.info("Updating orderId: {} with status: {}", orderId, status);

        // Cập nhật trạng thái đơn hàng trong cơ sở dữ liệu
        boolean success = orderService.updateOrderStatus(orderId, status);


        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false));
        }
    }
    
    @PostMapping("/submit-review/{productId}")
    @ResponseBody
    public ResponseEntity<?> submitReview(
            @PathVariable("productId") Long productId,
            @RequestParam("rating") int rating,
            @RequestParam("reviewText") String reviewText,
            @RequestParam(value = "imageFile", required = false) MultipartFile[] imageFiles,
            @RequestParam(value = "videoFile", required = false) MultipartFile[] videoFiles,
            HttpSession session) {
    	Logger logger = LoggerFactory.getLogger(OrderController.class);
    	logger.info("Số lượng ảnh: " + imageFiles.length);
        logger.info("Tên file ảnh: " + imageFiles);

        try {
            // Tạo thư mục nếu chưa tồn tại
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }
            
            // Lấy thông tin người dùng từ session, nếu không có thì gán User mặc định với ID = 1
            User user = userService.findById((Long) session.getAttribute("user0"));
            if (user == null) {
                user = userService.findById(1L);  // Lấy User mặc định có ID = 1 từ cơ sở dữ liệu
            }
            
            // Khởi tạo các biến để lưu URL ảnh và video
            String imageUrl = null;
            String videoUrl = null;

            // Xử lý và lưu ảnh/video
            if (imageFiles != null) {
                for (MultipartFile file : imageFiles) {
                    if (file != null && !file.isEmpty()) {
                        // Kiểm tra nếu ảnh đã tồn tại và bắt đầu bằng /images/
                        if (imageUrl != null && imageUrl.startsWith("/images/")) {
                            // Giữ nguyên ảnh cũ
                            continue;
                        }

                        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDirectory.getPath(), uniqueFileName);

                        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();

                        if (Arrays.asList("jpg", "jpeg", "png", "gif").contains(fileExtension)) {
                            file.transferTo(filePath.toFile());
                            imageUrl = "/images/" + uniqueFileName;
                        }
                    }
                }
            }

            if (videoFiles != null) {
                for (MultipartFile file : videoFiles) {
                    if (file != null && !file.isEmpty()) {
                        // Kiểm tra nếu video đã tồn tại và bắt đầu bằng /images/
                        if (videoUrl != null && videoUrl.startsWith("/images/")) {
                            // Giữ nguyên video cũ
                            continue;
                        }

                        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDirectory.getPath(), uniqueFileName);

                        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();

                        if (Arrays.asList("mp4", "avi", "mov").contains(fileExtension)) {
                            file.transferTo(filePath.toFile());
                            videoUrl = "/images/" + uniqueFileName;
                        }
                    }
                }
            }

            Product product = productService.getProductById(productId);
            
            if (user == null) {
                user = userService.findById(1L);
            }

            // Tìm kiếm đánh giá trước đó của user cho sản phẩm
            Review existingReview = reviewService.findByUserAndProduct(user, product);

            if (existingReview != null) {
                // Nếu tìm thấy đánh giá trước đó, cập nhật thông tin
                existingReview.setRating(rating);
                existingReview.setReviewText(reviewText);
                existingReview.setImageUrl(imageUrl);
                existingReview.setVideoUrl(videoUrl);
                reviewService.saveReview(existingReview);
            } else {
                // Nếu chưa có đánh giá trước đó, tạo đánh giá mới
                Review review = new Review();
                review.setProduct(product);
                review.setUser(user);
                review.setRating(rating);
                review.setReviewText(reviewText);
                review.setImageUrl(imageUrl);
                review.setVideoUrl(videoUrl);
                review.setCreatedAt(new Date());
                reviewService.saveReview(review);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Đánh giá đã được gửi thành công!"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Có lỗi xảy ra khi gửi đánh giá"
            ));
        }
    }

    @PostMapping("/save-payment")
    public ResponseEntity<Map<String, Object>> savePayment(
            @RequestParam("paymentImage") MultipartFile imageFile,
            @RequestParam("orderId") Long orderId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra và tạo thư mục nếu không tồn tại
            File uploadDirectory = new File(paymentUploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // Tạo tên file duy nhất
            String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(paymentUploadDir, uniqueFileName);

            // Lưu ảnh vào thư mục
            imageFile.transferTo(filePath.toFile());

            // Tìm và cập nhật order
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy đơn hàng");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Cập nhật thông tin thanh toán
            order.setImageUrl("/images/" + uniqueFileName);
            orderService.save(order);

            response.put("success", true);
            response.put("message", "Đã lưu ảnh thanh toán thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi khi lưu ảnh thanh toán: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/return-request")
    public ResponseEntity<?> handleReturnRequest(@RequestParam("orderId") Long orderId,
                                                  @RequestParam("returnReason") String returnReason,
                                                  @RequestParam(value = "returnImages", required = false) MultipartFile[] returnImages) throws IllegalStateException, IOException {
            // Tìm đơn hàng theo orderId
            Order order = orderService.getOrderById(orderId);

            // Kiểm tra xem đơn hàng đã có yêu cầu trả hàng chưa
            if (order.getReturnRequest() != null) {
                throw new BadRequestException("Đơn hàng này đã có yêu cầu trả hàng.");
            }

            // Tạo một đối tượng ReturnRequest mới
            ReturnRequest returnRequest = new ReturnRequest();
            returnRequest.setOrder(order);
            returnRequest.setReason(returnReason);
            returnRequest.setCreatedAt(LocalDateTime.now());
            returnRequest.setUpdatedAt(LocalDateTime.now());

            // Xử lý lưu trữ ảnh minh chứng
            if (returnImages != null && returnImages.length > 0) {
                // Tạo thư mục nếu chưa tồn tại
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs();
                }

                // Lưu ảnh và lấy đường dẫn
                String imageUrl = null;
                for (MultipartFile file : returnImages) {
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDirectory.getPath(), uniqueFileName);
                    file.transferTo(filePath.toFile());
                    imageUrl = "/images/" + uniqueFileName;
                }
                returnRequest.setImageUrl(imageUrl);
            }

            // Lưu yêu cầu trả hàng vào cơ sở dữ liệu
            returnRequestRepository.save(returnRequest);

            // Cập nhật trạng thái đơn hàng thành "Đang yêu cầu trả hàng"
            order.setStatus("Đang duyệt");
            orderService.save(order);

            // Trả về kết quả
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Yêu cầu trả hàng đã được gửi thành công!");
            return ResponseEntity.ok(response);
    }

}
