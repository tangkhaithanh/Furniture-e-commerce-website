package vn.iotstar.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.dto.OrderDetailDTO;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.User;

public interface OrderService {

	Order createOrder(User user, List<CartItem> cartItems);

	List<CartItem> getCartItemsByIds(List<Long> selectedItems, Long userId);

	Order getOrderById(Long orderId);

	Page<Order> findOrdersByStatusAndUserId(String status, Long userId, Pageable pageable);

	Page<Order> findOrdersByMultipleStatusesAndUserId(List<String> statuses, Long userId, Pageable pageable);
	
	Page<Order> findOrdersByUserId(Long userId, Pageable pageable);

	void save(Order order);
	// th�m: 

	boolean updateOrderStatus(Long orderId, String status);
	
	List<Order> getOrdersByStatus(String status);
	
	public List<Order> getAllOrders();
	
	Order confirmOrder(Long orderId);
	
	Order approveDelivery(Long orderId);
	
	List<Order> getOrdersByShipperAndStatus(Long shipperId, String status);
	
	Page<Order> getOrders(String search, String status, int page, int size);

	Order acceptOrder(Long orderId);
	
	Order rejectOrder(Long orderId);
	
	Order confirmDeliveredOrder(Long orderId);
	
	Page<Order> getAllOrdersWithPagination(Pageable pageable);
	
    Page<Order> getOrdersByStatusWithPagination(String status, Pageable pageable);
    
    Page<Order> searchOrdersWithPagination(String keyword, Pageable pageable);
    
    OrderDetailDTO getOrderDetail(Long orderId);
}