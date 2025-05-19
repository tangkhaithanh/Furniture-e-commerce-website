package vn.iotstar.services.impl;

import org.springframework.data.domain.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.dto.OrderDetailDTO;
import vn.iotstar.dto.OrderItemDTO;
import vn.iotstar.entity.*;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.ReturnRequestRepository;
import vn.iotstar.services.OrderService;
import vn.iotstar.services.UserService;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.NotificationRepository;
import vn.iotstar.repository.OrderItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{
	@Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired 
    private NotificationRepository notificationRepository;
    
    @Autowired
    private ReturnRequestRepository returnRequestRepository;
    @Override
    // Phương thức tạo đơn hàng từ giỏ hàng
    public Order createOrder(User user, List<CartItem> cartItems) {
        // Tính toán tổng tiền của đơn hàng
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            totalAmount = totalAmount.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus("Chờ xử lý");

        // Lưu đơn hàng vào database
        order = orderRepository.save(order);

        // Lưu các sản phẩm trong đơn hàng (OrderItem)
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            orderItemRepository.save(orderItem);
        }

        return order;
    }
    
    @Override
    public List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId) {
        // Khởi tạo Logger
        Logger logger = LoggerFactory.getLogger(getClass());

        // Kiểm tra nếu userId không hợp lệ hoặc danh sách selectedItems trống
        if (selectedItems == null || selectedItems.isEmpty()) {
            logger.warn("Danh sách selectedItems rỗng hoặc null.");
            return null;
        }

        // Lấy người dùng dựa trên userId
        User user = userService.findById(userId);

        if (user == null) {
            logger.error("Không tìm thấy người dùng với userId: {}", userId);
            return null;
        }

        // Log thông tin user
        logger.info("User tìm thấy: {} (userId: {})", user.getUsername(), userId);

        // Truy vấn CartItem từ cơ sở dữ liệu dựa trên danh sách selectedItems và userId
        List<CartItem> cartItems = cartItemRepository.findByIdInAndCartUserUserId(selectedItems, userId);

        // Log kết quả tìm được
        if (cartItems.isEmpty()) {
            logger.warn("Không tìm thấy CartItems cho userId: {}", userId);
        } else {
            logger.info("Tìm thấy {} CartItems.", cartItems.size());
        }

        return cartItems;
    }
    
    @Override
    // Phương thức để lấy đơn hàng theo ID
    public Order getOrderById(Long orderId) {
        // Kiểm tra nếu đơn hàng không tồn tại
        return orderRepository.findById(orderId).orElse(null);
    }
    
    @Override
    public Page<Order> findOrdersByMultipleStatusesAndUserId(List<String> statuses, Long userId, Pageable pageable) {
    	Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
        return orderRepository.findByStatusInAndUserUserId(statuses, userId, sortedByDate);
    }
    
 
    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }
    
    public Page<Order> findOrdersByUserId(Long userId, Pageable pageable) {
    	Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
        return orderRepository.findByUserUserIdAndStatusNot(userId, "chờ xử lý", sortedByDate);
	}
    
    
    @Override
    // Tìm danh sách đơn hàng của một user với trạng thái cụ thể
       public Page<Order> findOrdersByStatusAndUserId(String status, Long userId, Pageable pageable) {
       	 Pageable sortedByDate = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
       	    return orderRepository.findByStatusAndUserUserId(status, userId, sortedByDate);
       }
    
    @Override
    public boolean updateOrderStatus(Long orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            orderRepository.save(order); // Lưu lại thay đổi
            return true;
        }
        return false;
    }
    
	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public List<Order> getOrdersByStatus(String status) {
		 switch (status) 
		 {
	         case "pending":
	             return orderRepository.findByStatus("Chờ xác nhận");
	         case "shipping":
	             return orderRepository.findByStatus("Chờ duyệt đi giao");
	         case "delivered":
	             return orderRepository.findByStatus("Đã nhận hàng");
	         case "canceled":
	             return orderRepository.findByStatus("Hủy");
	         case "returned":
	             return orderRepository.findByStatus("Đang duyệt");
	         case "confirmed":
	        	 return orderRepository.findByStatus("Đã xác nhận");
	         default:
	             return orderRepository.findAll();
		 }
	}

	@Override
	public Order confirmOrder(Long orderId) {
		// Tìm đơn hàng theo orderId
        Order order = orderRepository.findById(orderId).orElse(null);
        
        // Kiểm tra xem đơn hàng có tồn tại và đang ở trạng thái "Chờ xác nhận"
        if (order != null && "Chờ xác nhận".equals(order.getStatus())) {
            // Cập nhật trạng thái của đơn hàng thành "Đã xác nhận"
            order.setStatus("Đã xác nhận");
            
            // Lưu lại đơn hàng đã cập nhật vào cơ sở dữ liệu
            orderRepository.save(order);
            
            // Trả về đơn hàng đã được xác nhận
            return order;
        }
        
        // Nếu đơn hàng không tồn tại hoặc không ở trạng thái "Chờ xác nhận"
        return null;
	}

	@Override
	public Order approveDelivery(Long orderId) {
		Order order = orderRepository.findOrderById(orderId);
	    if (order != null && "Chờ duyệt đi giao".equals(order.getStatus())) {
	        order.setStatus("Đang giao"); // Hoặc trạng thái phù hợp trong hệ thống của bạn
	        return orderRepository.save(order);
	    }
	    return null;
	}

	@Override
	public List<Order> getOrdersByShipperAndStatus(Long shipperId, String status)
	{
		 // Tìm các notification của shipper và status đã nhận giao
        List<Notification> notifications = notificationRepository. findByUser_UserIdAndStatus(shipperId, "đã nhận giao");
        
        // Lấy các orderId từ notifications
        List<Long> orderIds = notifications.stream()
                .map(notification -> notification.getOrder().getId())
                .collect(Collectors.toList());
        
        // Trả về các order có id nằm trong danh sách và có status được chỉ định
        return orderRepository.findByIdInAndStatus(orderIds, status);
	}

	@Override
	public Page<Order> getOrders(String search, String status, int page, int size) 
	{
		Pageable pageable = PageRequest.of(page, size);
	    
	    // Nếu không có bất kỳ điều kiện lọc nào
	    if (search.isEmpty() && status.isEmpty()) {
	        return orderRepository.findAll(pageable);
	    }
	    
	    // Nếu chỉ có search
	    if (!search.isEmpty() && status.isEmpty()) {
	        return orderRepository.findBySearchOnly(search, pageable);
	    }
	    
	    // Nếu có cả search và status
	    if (!search.isEmpty() && !status.isEmpty()) {
	        return orderRepository.findBySearchCriteria(search, status, pageable);
	    }
	    
	    // Nếu chỉ có status
	    if (search.isEmpty() && !status.isEmpty()) {
	        return orderRepository.findBySearchCriteria("", status, pageable);
	    }
	    
	    return orderRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public Order acceptOrder(Long orderId)
	{
		Order order = orderRepository.findById(orderId).orElse(null);
		
        if (order != null && order.getStatus().equals("Đang duyệt")) {
        	returnRequestRepository.deleteByOrderId(order.getId());
            order.setStatus("Trả hàng");  // Hoặc trạng thái xác nhận phù hợp
            return orderRepository.save(order);
        }
        return null;  // Trả về null nếu không thể xác nhận
	}

	@Override
	@Transactional
	public Order rejectOrder(Long orderId) 
	{
		Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getStatus().equals("Đang duyệt")) 
        {
        	returnRequestRepository.deleteByOrderId(order.getId());
            order.setStatus("Từ chối trả");  // Hoặc trạng thái từ chối phù hợp
            return orderRepository.save(order);
        }
        return null;  // Trả về null nếu không thể từ chối
	}

	@Override
	public Order confirmDeliveredOrder(Long orderId) {
		Optional<Order> optionalOrder = orderRepository.findById(orderId);
	    
	    if (optionalOrder.isPresent()) {
	        Order order = optionalOrder.get();
	        
	        if (order.getStatus().equals("Đã nhận hàng")) {
	            order.setStatus("Đã giao");
	            return orderRepository.save(order);
	        }
	    }
	    return null;
	}

	@Override
	public Page<Order> getAllOrdersWithPagination(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}

	@Override
	public Page<Order> getOrdersByStatusWithPagination(String status, Pageable pageable)
	{
		 switch (status) {
	        case "pending":
	            return orderRepository.findByStatus("Chờ xác nhận", pageable);
	        case "shipping":
	            return orderRepository.findByStatus("Chờ duyệt đi giao", pageable);
	        case "delivered":
	            return orderRepository.findByStatus("Đã nhận hàng", pageable);
	        case "canceled":
	            return orderRepository.findByStatus("Hủy", pageable);
	        case "returned":
	            return orderRepository.findByStatus("Đang duyệt", pageable);
	        case "confirmed":
	            return orderRepository.findByStatus("Đã xác nhận", pageable);
	        default:
	            return orderRepository.findAll(pageable);
	    }
	}

	@Override
	public Page<Order> searchOrdersWithPagination(String keyword, Pageable pageable) {
		 Long id = null;
		    try {
		        id = Long.parseLong(keyword);
		    } catch (NumberFormatException e) {
		        // Nếu không thể chuyển đổi keyword thành Long, bỏ qua và chỉ tìm kiếm theo tên người dùng
		    }
		    return orderRepository.searchOrders(id, keyword, pageable);
	}

	@Override
	public OrderDetailDTO getOrderDetail(Long orderId) 
	{
		Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));
	            
	        OrderDetailDTO dto = new OrderDetailDTO();
	        dto.setOrderId(order.getId());
	        dto.setTotalAmount(order.getTotalAmount());
	        dto.setStatus(order.getStatus());
	        dto.setCreatedAt(order.getCreatedAt());
	        dto.setPaymentMethod(order.getPaymentMethod());
	        dto.setCustomerName(order.getUser().getUsername());
	        List<OrderItemDTO> items = order.getOrderItems().stream()
	            .map(item -> new OrderItemDTO(
	                item.getProduct().getName(),
	                item.getQuantity(),
	                item.getPrice(),
	                item.getProduct().getImageUrl()
	            ))
	            .collect(Collectors.toList());
	        
	        dto.setItems(items);
	        return dto;
	}
}
