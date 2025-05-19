package vn.iotstar.controllers.vendor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.iotstar.dto.OrderDetailDTO;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Notification;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.ReturnRequest;
import vn.iotstar.services.NotificationService;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.ReturnRequestService;
import vn.iotstar.utils.ApiResponse;

@Controller
public class VendorOrderController 
{
	@Autowired
	private OrderService orderService;
	@Autowired 
	private NotificationService notificationService;
	@Autowired 
	private ReturnRequestService returnRequestService;
	@GetMapping("/vendor/orders")
	public String viewOrders(
	        @RequestParam(value = "status", required = false) String status,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "5") int size,
	        Model model) {

	        // Tạo Pageable object cho phân trang
	        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "id"));
	        
	        // Khai báo Page thay vì List để hỗ trợ phân trang
	        Page<Order> orderPage;	
	        // Lấy đơn hàng theo trạng thái với phân trang
	        if (status == null || status.isEmpty()) {
	            orderPage = orderService.getAllOrdersWithPagination(pageable);
	        } else {
	            orderPage = orderService.getOrdersByStatusWithPagination(status, pageable);
	        }

	        List<String> formattedDates = new ArrayList<>();
	        List<String> phoneNumbers = new ArrayList<>();
	        List<Address> defaultAddresses = new ArrayList<>();

	        // Xử lý dữ liệu cho từng đơn hàng trong trang hiện tại
	        for (Order order : orderPage.getContent()) {
	            String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	            formattedDates.add(formattedDate);
	            
	            String phoneNumber = null;
	            Address defaultAddress = null;

	            if (order.getUser() != null && order.getUser().getAddresses() != null) {
	                List<Address> addresses = order.getUser().getAddresses();
	                for (Address address : addresses) {
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

	        // Thêm thông tin phân trang vào model
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", orderPage.getTotalPages());
	        model.addAttribute("totalItems", orderPage.getTotalElements());
	        model.addAttribute("orders", orderPage.getContent());
	        model.addAttribute("formattedDates", formattedDates);
	        model.addAttribute("phoneNumbers", phoneNumbers);
	        model.addAttribute("defaultAddresses", defaultAddresses);

	        // Xử lý thông báo
	        List<String> statuses = Arrays.asList("mới", "đã nhận giao", "trả hàng", "đã giao xong", "đã nhận hàng");
	        Long unreadNewNotificationsCount = notificationService.countNotificationsByStatusAndNotRead(statuses);
	        List<Notification> notifications = notificationService.getNotificationsByStatus(statuses);
	        
	        model.addAttribute("unreadNotificationCount", unreadNewNotificationsCount);
	        model.addAttribute("notifications", notifications);
	        model.addAttribute("currentStatus", status); // Thêm trạng thái hiện tại để duy trì filter khi phân trang

	        return "ManageOrder";
	    }
	
	@PostMapping("/vendor/order/confirm/{orderId}")
    public ResponseEntity<ApiResponse> confirmOrder(@PathVariable("orderId") Long orderId)
	{
		
        Order order = orderService.confirmOrder(orderId); // Gọi service để xác nhận đơn hàng

        if (order != null)
        {
            // Trả về ApiResponse thành công với thông tin đơn hàng đã xác nhận
            return ResponseEntity.ok().body(new ApiResponse(true, "Đơn hàng đã được xác nhận", null));
        } 
        else 
        {
            // Trả về ApiResponse thất bại nếu không thể xác nhận đơn hàng
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Có lỗi xảy ra khi xác nhận đơn hàng", null));
        }
    }
	
	// controller chuyển duyệt đơn hàng đi giao:
	@PostMapping("/vendor/order/approve-delivery/{orderId}")
	public ResponseEntity<ApiResponse> approveDelivery(@PathVariable("orderId") Long orderId) {
	    try {
	        Order order = orderService.approveDelivery(orderId); // Gọi service để duyệt đơn hàng đi giao

	        if (order != null) {
	            // Trả về ApiResponse thành công với thông tin đơn hàng đã duyệt
	            return ResponseEntity.ok()
	                .body(new ApiResponse(true, "Đơn hàng đã được duyệt đi giao thành công", null));
	        } else {
	            // Trả về ApiResponse thất bại nếu không thể duyệt đơn hàng
	            return ResponseEntity.badRequest()
	                .body(new ApiResponse(false, "Không tìm thấy đơn hàng hoặc đơn hàng không ở trạng thái chờ duyệt", null));
	        }
	    } catch (Exception e) {
	        // Xử lý các exception và trả về thông báo lỗi phù hợp
	        return ResponseEntity.badRequest()
	            .body(new ApiResponse(false, "Có lỗi xảy ra khi duyệt đơn hàng: " + e.getMessage(), null));
	    }
	}
	
	//controller chấp nhận đơn hàng trả lại:
	 @PostMapping("/vendor/order/accept/{orderId}")
	    public ResponseEntity<ApiResponse> acceptOrder(@PathVariable("orderId") Long orderId) 
	 {
	        try {
	            Order order = orderService.acceptOrder(orderId);  // Gọi service để chấp nhận đơn hàng

	            if (order != null) {
	                // Trả về ApiResponse thành công với thông tin đơn hàng đã chấp nhận
	                return ResponseEntity.ok()
	                        .body(new ApiResponse(true, "Đơn hàng đã được chấp nhận", null));
	            } else {
	                // Trả về ApiResponse thất bại nếu không thể chấp nhận đơn hàng
	                return ResponseEntity.badRequest()
	                        .body(new ApiResponse(false, "Không thể chấp nhận đơn hàng", null));
	            }
	        } catch (Exception e) {
	            // Xử lý các exception và trả về thông báo lỗi phù hợp
	            return ResponseEntity.badRequest()
	                    .body(new ApiResponse(false, "Có lỗi xảy ra khi chấp nhận đơn hàng: " + e.getMessage(), null));
	        }
	  }
	 
	 //controller từ chối đơn hàng trả lại:
	 @PostMapping("/vendor/order/reject/{orderId}")
	    public ResponseEntity<ApiResponse> rejectOrder(@PathVariable("orderId") Long orderId) 
	 {
	        try {
	            Order order = orderService.rejectOrder(orderId);  // Gọi service để từ chối đơn hàng

	            if (order != null) {
	                // Trả về ApiResponse thành công với thông tin đơn hàng đã bị từ chối
	                return ResponseEntity.ok()
	                        .body(new ApiResponse(true, "Đơn hàng đã bị từ chối", null));
	            } else {
	                // Trả về ApiResponse thất bại nếu không thể từ chối đơn hàng
	                return ResponseEntity.badRequest()
	                        .body(new ApiResponse(false, "Không thể từ chối đơn hàng", null));
	            }
	        } catch (Exception e) {
	            // Xử lý các exception và trả về thông báo lỗi phù hợp
	            return ResponseEntity.badRequest()
	                    .body(new ApiResponse(false, "Có lỗi xảy ra khi từ chối đơn hàng: " + e.getMessage(), null));
	        }
	    }
	 // Xác nhận đơn hàng đã giao:
	 @PostMapping("/vendor/order/complete/{orderId}")
	 public ResponseEntity<ApiResponse> confirmDeliveredOrder(@PathVariable("orderId") Long orderId) {
	     try {
	         Order order = orderService.confirmDeliveredOrder(orderId);
	         
	         if (order != null) {
	             return ResponseEntity.ok()
	                 .body(new ApiResponse(true, "Đơn hàng đã được xác nhận giao thành công", null));
	         } else {
	             return ResponseEntity.badRequest()
	                 .body(new ApiResponse(false, "Không tìm thấy đơn hàng hoặc đơn hàng không ở trạng thái đang giao", null));
	         }
	     } catch (Exception e) {
	         return ResponseEntity.badRequest()
	             .body(new ApiResponse(false, "Có lỗi xảy ra khi xác nhận giao đơn hàng: " + e.getMessage(), null));
	     }
	 }
	 
	 @GetMapping("/vendor/order/returned")
	 public String returnedOrder(
	     @RequestParam(value = "page", defaultValue = "0") int page,
	     @RequestParam(value = "size", defaultValue = "10") int size,
	     Model model) {
	         
	     // Tạo Pageable object
	     Pageable pageable = PageRequest.of(page, size);
	     
	     // Lấy return requests với phân trang
	     Page<ReturnRequest> returnRequestPage = returnRequestService.getReturnRequestsWithPagination(pageable);
	     
	     // Thêm thông tin phân trang vào model
	     model.addAttribute("returnRequests", returnRequestPage.getContent());
	     model.addAttribute("currentPage", page);
	     model.addAttribute("totalPages", returnRequestPage.getTotalPages());
	     model.addAttribute("totalItems", returnRequestPage.getTotalElements());
	     
	     // Xử lý thông báo
	     List<String> statuses = Arrays.asList("mới", "đã nhận giao", "trả hàng", "đã giao xong", "đã nhận hàng");
	     Long unreadNewNotificationsCount = notificationService.countNotificationsByStatusAndNotRead(statuses);
	     List<Notification> notifications = notificationService.getNotificationsByStatus(statuses);
	     
	     model.addAttribute("unreadNotificationCount", unreadNewNotificationsCount);
	     model.addAttribute("notifications", notifications);
	     
	     return "ManageOrder";
	 }
	 
	 @GetMapping("/vendor/orders/search")
	 public String searchOrders(
	         @RequestParam("keyword") String keyword,
	         @RequestParam(value = "page", defaultValue = "0") int page,
	         @RequestParam(value = "size", defaultValue = "5") int size,
	         Model model) {

	     // Tạo Pageable object cho phân trang
	     Pageable pageable = PageRequest.of(page, size);

	     // Gọi phương thức tìm kiếm đơn hàng theo từ khóa và phân trang
	     Page<Order> orderPage = orderService.searchOrdersWithPagination(keyword, pageable);

	     // Xử lý dữ liệu tương tự như phương thức viewOrders
	     List<String> formattedDates = new ArrayList<>();
	     List<String> phoneNumbers = new ArrayList<>();
	     List<Address> defaultAddresses = new ArrayList<>();

	     for (Order order : orderPage.getContent()) {
	            String formattedDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	            formattedDates.add(formattedDate);
	            
	            String phoneNumber = null;
	            Address defaultAddress = null;

	            if (order.getUser() != null && order.getUser().getAddresses() != null) {
	                List<Address> addresses = order.getUser().getAddresses();
	                for (Address address : addresses) {
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
	     // Thêm thông tin phân trang và dữ liệu vào model
	     model.addAttribute("currentPage", page);
	     model.addAttribute("totalPages", orderPage.getTotalPages());
	     model.addAttribute("totalItems", orderPage.getTotalElements());
	     model.addAttribute("orders", orderPage.getContent());
	     model.addAttribute("formattedDates", formattedDates);
	     model.addAttribute("phoneNumbers", phoneNumbers);
	     model.addAttribute("defaultAddresses", defaultAddresses);
	     model.addAttribute("keyword", keyword); // Thêm từ khóa tìm kiếm vào model

	     // Xử lý thông báo
	     List<String> statuses = Arrays.asList("mới", "đã nhận giao", "trả hàng", "đã giao xong", "đã nhận hàng");
	     Long unreadNewNotificationsCount = notificationService.countNotificationsByStatusAndNotRead(statuses);
	     List<Notification> notifications = notificationService.getNotificationsByStatus(statuses); 
	     model.addAttribute("unreadNotificationCount", unreadNewNotificationsCount);
	     model.addAttribute("notifications", notifications);
	     return "ManageOrder";
	 }
	 
	 @GetMapping("/vendor/orders/{orderId}/details")
	 @ResponseBody
	 public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) 
	 {
	     try
	     {
	           	OrderDetailDTO orderDetail = orderService.getOrderDetail(orderId);
	            return ResponseEntity.ok(orderDetail);
	     } 
	     catch (Exception e)
	     {
	            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
	     }
	 }
}
