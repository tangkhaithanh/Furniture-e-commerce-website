package vn.iotstar.controllers;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Coupon;
import vn.iotstar.entity.Notification;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderItem;
import vn.iotstar.entity.User;
import vn.iotstar.entity.UserCoupon;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.AddressService;
import vn.iotstar.services.CartItemService;
import vn.iotstar.services.CartService;
import vn.iotstar.services.CouponService;
import vn.iotstar.services.NotificationService;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.ReviewService;
import vn.iotstar.services.UserCouponService;
import vn.iotstar.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/order")
public class OrderViewController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartService cartService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private UserCouponService userCouponService;
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping("/summary/{orderId}")
    public String orderSummary(@PathVariable("orderId") Long orderId, @RequestParam(required = false) List<Long> selectedItems, Model model, HttpSession session) {
    	// Khởi tạo Logger
        Logger logger = LoggerFactory.getLogger(OrderController.class);
     // In thử selectedItems vào log để xem kết quả
        logger.info("Selected items: " + (selectedItems != null ? selectedItems.toString() : "No selected items"));
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            userId = 1L;  // Giả sử là userId = 1 nếu không tìm thấy
        }

        // Kiểm tra quyền truy cập: đảm bảo đơn hàng thuộc về người dùng
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUser().getUserId().equals(userId)) {
            model.addAttribute("message", "Không tìm thấy đơn hàng hoặc bạn không có quyền truy cập.");
            return "error";  // Trang lỗi nếu không hợp lệ
        }
        
     // Lấy địa chỉ mặc định của người dùng
        Address defaultAddress = addressService.getDefaultAddress(userId);
        
     // Lấy danh sách tất cả địa chỉ của người dùng
        List<Address> allAddresses = addressService.getAddressesByUserId(userId);
        
        // Thêm thông tin đơn hàng và địa chỉ vào model
        model.addAttribute("selectedItem", selectedItems);
        model.addAttribute("order", order);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("allAddresses", allAddresses);

        return "order_summary";
    }
   
    @GetMapping("/purchase")
    public String viewOrders(
            @RequestParam(name = "status", defaultValue = "ALL") String status,
            @RequestParam(name = "page", defaultValue = "0") int page, // Trang hiện tại
            @RequestParam(name = "size", defaultValue = "10") int size, // Kích thước mỗi trang
            Model model,
            HttpSession session) {

        // Lấy userId từ session
        Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            userId = 1L; // Giá trị mặc định nếu userId không có trong session
        }

        // Khởi tạo đối tượng Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("createdAt"))); // Sắp xếp theo ngày đặt hàng

        // Lấy danh sách đơn hàng theo trạng thái với phân trang
        Page<Order> ordersPage;

        switch (status) {
            case "1":
                ordersPage = orderService.findOrdersByStatusAndUserId("chờ xác nhận", userId, pageable);
                break;
            case "2":
                ordersPage = orderService.findOrdersByStatusAndUserId("đã xác nhận", userId, pageable);
                break;
            case "3":
                ordersPage = orderService.findOrdersByMultipleStatusesAndUserId(Arrays.asList("đang giao", "Đã nhận hàng"), userId, pageable);
                break;
            case "4":
                // Trạng thái đã giao và đang duyệt
                ordersPage = orderService.findOrdersByMultipleStatusesAndUserId(Arrays.asList("đã giao", "đang duyệt","Từ chối trả"), userId, pageable);
                break;
            case "5":
                ordersPage = orderService.findOrdersByStatusAndUserId("hủy", userId, pageable);
                break;
            case "6":
                ordersPage = orderService.findOrdersByStatusAndUserId("trả hàng", userId, pageable);
                break;
            case "0":
            default:
                ordersPage = orderService.findOrdersByUserId(userId, pageable);
                break;
        }
        
        for (Order order : ordersPage.getContent()) {
            for (OrderItem item : order.getOrderItems()) {
                boolean reviewed = reviewService.hasUserReviewedProduct(userId, item.getProduct().getId());
                item.setReviewed(reviewed);
            }
        }
        
        // Thêm danh sách đơn hàng và thông tin phân trang vào model
        model.addAttribute("orders", ordersPage.getContent());  // Lấy danh sách đơn hàng từ Page
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", ordersPage.getTotalPages()); // Tổng số trang
        model.addAttribute("totalItems", ordersPage.getTotalElements()); // Tổng số phần tử
        model.addAttribute("status", status); // Trạng thái đơn hàng
        model.addAttribute("size", size);
        // Trả về view hiển thị danh sách đơn hàng
        return "orderPurchase";
    }

    
	@GetMapping("/purchases")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("selectedItem") List<Long> selectedItems,
                                    @RequestParam("paymentMethod") String paymentMethod,
                                    @RequestParam("newTotalAmount") BigDecimal newTotalAmount,
                                    @RequestParam(value = "voucherCode", required = false) String voucherCode,
                                    Model model,
                                    HttpSession session) {
    	Long userId = (Long) session.getAttribute("user0");
        if (userId == null) {
            userId = 1L;
        }
        User user = userService.findById(userId);
        // Lấy đơn hàng theo orderId
        Order order = orderService.getOrderById(orderId);
        if (voucherCode != null && !voucherCode.isEmpty()) {
            Coupon coupon = couponService.findByCode(voucherCode);
            if (coupon != null) {
                // Kiểm tra xem người dùng đã sử dụng mã giảm giá này trước đó chưa
                boolean alreadyUsed = user.getUserCoupons().stream()
                        .anyMatch(uc -> uc.getCoupon().equals(coupon));

                if (!alreadyUsed) {
                    // Thêm bản ghi vào bảng UserCoupon
                    UserCoupon userCoupon = new UserCoupon();
                    userCoupon.setUser(user);
                    userCoupon.setCoupon(coupon);
                    userCoupon.setUsedAt(LocalDateTime.now());
                    userCouponService.saveUserCoupon(userCoupon);

                    // Giảm số lượng mã giảm giá đi 1
                    coupon.setQuantity(coupon.getQuantity() - 1);
                    couponService.saveCoupon(coupon);
                }
            }
        }
        
        if (order != null) {
            // Cập nhật trạng thái của đơn hàng
            order.setStatus("Chờ xác nhận");
            order.setPaymentMethod(paymentMethod);
            order.setTotalAmount(newTotalAmount);
            orderService.save(order);
         // Tạo thông báo mới sau khi cập nhật trạng thái đơn hàng
            Notification notification = new Notification();
            Date date = new Date();
            // Cập nhật thông tin cho notification
            notification.setUser(user);
            notification.setOrder(order);
            notification.setTimestamp(date);  // Thời gian hiện tại
            notification.setMessage("Đơn hàng mới");
            notification.setRead(false);  // Mặc định chưa đọc
            notification.setStatus("mới");  // Trạng thái mới
            
            // Lưu thông báo vào cơ sở dữ liệu
            notificationService.save(notification);  // Lưu thông báo vào DB
            
        }     
        Cart cart = cartService.getCartByUserId(userId);

        if (cart != null) {
            // Duyệt qua danh sách selectedItems và xóa các sản phẩm trong giỏ hàng

            for (Long selectedItemId : selectedItems) {
               cartItemService.deleteCardItem(selectedItemId);
            }
            // Lưu lại giỏ hàng sau khi xóa sản phẩm
            cartService.save(cart);
        }
        // Sau khi cập nhật, chuyển hướng đến trang danh sách đơn hàng
        return "redirect:/order/purchase?status=1";
    }
}
