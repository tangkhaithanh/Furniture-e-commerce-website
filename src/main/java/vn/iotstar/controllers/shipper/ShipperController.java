package vn.iotstar.controllers.shipper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.iotstar.dto.NotificationRequest;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Notification;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.NotificationRepository;
import vn.iotstar.services.NotificationService;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.ProductService;
import vn.iotstar.services.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/shipper")
public class ShipperController {

	@Autowired 
	private OrderService orderService;
	@Autowired 
	private NotificationService notificationService;
	@Autowired 
	private NotificationRepository notificationRepository;
	@Autowired 
	private UserService userService;
	@Autowired
	private ProductService productService;
	@GetMapping()
	public String shipperHomePage()
	{
		
		return "Shipper";
	}
	
	@GetMapping("/remaining_orders")
	public String shipperHomePage(Model model) 
	{
	    // Lấy danh sách đơn hàng có trạng thái "confirmed"
	    List<Order> confirmedOrders = orderService.getOrdersByStatus("confirmed");
	    
	    // Tạo các danh sách để chứa thông tin cần thiết cho view
	    List<String> formattedDates = new ArrayList<>();
	    List<String> phoneNumbers = new ArrayList<>();
	    List<Address> defaultAddresses = new ArrayList<>(); // Lưu các địa chỉ mặc định cho mỗi đơn hàng
	    List<String> paymentMethod= new ArrayList<>();
	    // Duyệt qua danh sách các đơn hàng
	    for (Order order : confirmedOrders) {
	        // Định dạng ngày tạo đơn hàng
	        String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        formattedDates.add(formattedDate);
	        
	        // Lấy số điện thoại từ địa chỉ mặc định
	        String phoneNumber = null;
	        Address defaultAddress = null; // Lưu địa chỉ mặc định

	        if (order.getUser() != null) {
	            List<Address> addresses = order.getUser().getAddresses();
	            for (Address address : addresses) {
	                if (address.isDefault()) {
	                    defaultAddress = address;  // Lưu địa chỉ mặc định
	                    phoneNumber = address.getPhoneNumber();  // Lấy số điện thoại
	                    break;  // Dừng vòng lặp khi đã tìm thấy địa chỉ mặc định
	                }
	            }
	        }
	        // Thêm địa chỉ mặc định và số điện thoại vào các danh sách
	        defaultAddresses.add(defaultAddress);
	        phoneNumbers.add(phoneNumber);
	        paymentMethod.add(order.getPaymentMethod());
	    }
	    // Thêm các thuộc tính vào model để Thymeleaf có thể sử dụng
	    model.addAttribute("orders", confirmedOrders);
	    model.addAttribute("formattedDates", formattedDates);
	    model.addAttribute("phoneNumbers", phoneNumbers);
	    model.addAttribute("defaultAddresses", defaultAddresses); // Gửi địa chỉ mặc định vào view
	    model.addAttribute("paymentMethod",paymentMethod);

	    // Trả về tên của trang view
	    return "Shipper";
	}
	@PostMapping("/createNotification")
	public ResponseEntity<Map<String, Object>> createNotification(@RequestBody NotificationRequest request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Notification notification = new Notification();
	        User shipper= userService.findById(request.getShipperId());
	        Order order=orderService.getOrderById(request.getOrderId());
	        notification.setUser(shipper);
	        notification.setOrder(order);
	        notification.setMessage("Đơn hàng " + order.getId() + " đã được shipper " + shipper.getUsername() + " nhận giao.");
	        notification.setTimestamp(new Date());  // Gán thời gian hiện tại
	        notification.setStatus("đã nhận giao");
	        notificationService.save(notification);
	        // Cập nhật lại trạng thái đơn hàng là "chờ duyệt đi giao" 
	        orderService.updateOrderStatus(request.getOrderId(), "Chờ duyệt đi giao");
	        response.put("success", true);
	        response.put("message", "Thông báo đã được tạo thành công.");
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "Có lỗi xảy ra khi tạo thông báo.");
	    }
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/confirmed-orders")
	public String getConfirmedOrders(Model model, HttpSession session) {
		Long shipperId = (Long) session.getAttribute("user0");
		List<Order> confirmedOrders = orderService.getOrdersByShipperAndStatus(shipperId, "Đã nhận hàng");
		List<String> formattedDates = new ArrayList<>();
        List<String> phoneNumbers = new ArrayList<>();
        List<Address> defaultAddresses = new ArrayList<>();
        List<String> paymentMethod= new ArrayList<>();
        for (Order order : confirmedOrders) {
            // Format order creation date
            String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            formattedDates.add(formattedDate);
            
            // Get phone number from default address
            String phoneNumber = null;
            Address defaultAddress = null;

            if (order.getUser() != null) {
                for (Address address : order.getUser().getAddresses()) {
                    if (address.isDefault()) {
                        defaultAddress = address;
                        phoneNumber = address.getPhoneNumber();
                        break;
                    }
                }
            }

            defaultAddresses.add(defaultAddress);
            phoneNumbers.add(phoneNumber);
            paymentMethod.add(order.getPaymentMethod());
        }

        model.addAttribute("orders", confirmedOrders);
        model.addAttribute("formattedDates", formattedDates);
        model.addAttribute("phoneNumbers", phoneNumbers);
        model.addAttribute("defaultAddresses", defaultAddresses);
        model.addAttribute("paymentMethod",paymentMethod);
        return "Shipper";
	}
	
	@PostMapping("/complete-order/{orderId}")
	public ResponseEntity<Map<String, Object>> completeOrder(@PathVariable Long orderId, HttpSession session) {
	    Long shipperId = (Long) session.getAttribute("user0");
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Order order = orderService.getOrderById(orderId);
	        if (order != null && "Đã nhận hàng".equals(order.getStatus())) {
	            List<OrderItem> orderItems = order.getOrderItems();
	            for (OrderItem item : orderItems) {
	                Product product = item.getProduct();
	                int currentQuantity = product.getQuantity();
	                int orderedQuantity = item.getQuantity();
	              
	                if (currentQuantity >= orderedQuantity) {
	                    product.setQuantity(currentQuantity - orderedQuantity);
	                    productService.save(product);
	                } else {
	                    response.put("success", false);
	                    response.put("message", "Số lượng sản phẩm " + product.getName() + " trong kho không đủ.");
	                    return ResponseEntity.ok(response);
	                }
	            }

	            // Cập nhật trạng thái đơn hàng
	            order.setStatus("Đã giao");
	            orderService.save(order);

	            // Tạo thông báo đã giao
	            Notification notification = new Notification();
	            notification.setOrder(order);
	            notification.setUser(order.getUser());
	            User shipper = userService.findById(shipperId);
	            String message = "Đơn hàng " + orderId + " đã được giao thành công bởi shipper " + shipper.getUsername() + ".";
	            notification.setMessage(message);
	            notification.setTimestamp(new Date());
	            notification.setStatus("đã giao xong");
	            notificationService.save(notification);

	            response.put("success", true);
	            response.put("message", "Đơn hàng đã được giao thành công và số lượng sản phẩm đã được cập nhật.");
	        } else {
	            response.put("success", false);
	            response.put("message", "Không tìm thấy đơn hàng hoặc trạng thái không hợp lệ.");
	        }
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "Có lỗi xảy ra khi hoàn thành đơn hàng.");
	        e.printStackTrace();
	    }
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/completed-orders")
	public String getCompletedOrders(Model model, HttpSession session) {
		Long shipperId = (Long) session.getAttribute("user0");
	    
	    // Lấy các đơn hàng đã hoàn thành của shipper
	    List<Order> completedOrders = notificationRepository
	            .findDistinctOrdersByUser_UserIdAndOrderStatus(shipperId, "Đã nhận hàng")
	            .stream()
	            .map(Notification::getOrder)
	            .distinct()
	            .collect(Collectors.toList());
	    
	    // Chuẩn bị dữ liệu cho view
	    List<String> formattedDates = new ArrayList<>();
	    List<String> phoneNumbers = new ArrayList<>();
	    List<Address> defaultAddresses = new ArrayList<>();

	    for (Order order : completedOrders) {
	        // Format order creation date
	        String formattedDate = order.getCreatedAt()
	            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        formattedDates.add(formattedDate);
	        
	        // Get phone number from default address
	        String phoneNumber = null;
	        Address defaultAddress = null;

	        if (order.getUser() != null) {
	            for (Address address : order.getUser().getAddresses()) {
	                if (address.isDefault()) {
	                    defaultAddress = address;
	                    phoneNumber = address.getPhoneNumber();
	                    break;
	                }
	            }
	        }

	        defaultAddresses.add(defaultAddress);
	        phoneNumbers.add(phoneNumber);
	    }

	    // Add attributes to model
	    model.addAttribute("orders", completedOrders);
	    model.addAttribute("formattedDates", formattedDates);
	    model.addAttribute("phoneNumbers", phoneNumbers);
	    model.addAttribute("defaultAddresses", defaultAddresses);
	    
	    return "Shipper";
	}
	@GetMapping("/in-progress-orders")
	public String getShippingOrders(Model model, HttpSession session) {
	    // Lấy ID của shipper từ session
	    Long shipperId = (Long) session.getAttribute("user0");
	    
	    // Lấy danh sách đơn hàng đang giao bởi shipper này
	    List<Order> shippingOrders = orderService.getOrdersByShipperAndStatus(shipperId, "Đang giao");

	    // Tạo các danh sách chứa thông tin cần thiết
	    List<String> formattedDates = new ArrayList<>();
	    List<String> phoneNumbers = new ArrayList<>();
	    List<Address> defaultAddresses = new ArrayList<>();
	    List<String> paymentMethod = new ArrayList<>();

	    for (Order order : shippingOrders) {
	        // Định dạng ngày
	        String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        formattedDates.add(formattedDate);

	        // Lấy số điện thoại và địa chỉ mặc định
	        String phoneNumber = null;
	        Address defaultAddress = null;

	        if (order.getUser() != null && order.getUser().getAddresses() != null) {
	            for (Address address : order.getUser().getAddresses()) {
	                if (address.isDefault()) {
	                    defaultAddress = address;
	                    phoneNumber = address.getPhoneNumber();
	                    break;
	                }
	            }
	        }

	        defaultAddresses.add(defaultAddress);
	        phoneNumbers.add(phoneNumber);
	        paymentMethod.add(order.getPaymentMethod());
	    }

	    model.addAttribute("orders", shippingOrders);
	    model.addAttribute("formattedDates", formattedDates);
	    model.addAttribute("phoneNumbers", phoneNumbers);
	    model.addAttribute("defaultAddresses", defaultAddresses);
	    model.addAttribute("paymentMethod", paymentMethod);
	    return "Shipper"; // Tên template Thymeleaf
	}
}
