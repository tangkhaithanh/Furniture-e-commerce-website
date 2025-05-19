package vn.iotstar.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Order;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.repository.ReviewRepository;
import vn.iotstar.repository.UserRepository;

@Service
public class DashBoardService {

	@Autowired 
	private OrderRepository orderRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	// tổng số đơn hàng đã bán:
	public long getTotalOrders() 
	{
		 return orderRepository.countByStatus("Đã giao");
	}
	
	// tổng doanh thu:
	public BigDecimal getTotalRevenue() {
        return orderRepository.sumTotalRevenue(); // Lấy tổng doanh thu từ các đơn hàng đã hoàn thành
    }
	
	/*
	 * //tổng số khách hàng: public long getTotalCustomers() { return
	 * userRepository.countUsersByRole(); }
	 */
	
	// lấy mức độ hài lòng của khách hàng:
	
	 public double getCustomerSatisfaction() {
	        List<Order> orders = orderRepository.findAll();

	        double totalSatisfaction = 0.0;
	        int totalOrdersWithReviews = 0;

	        // Lặp qua từng đơn hàng
	        for (Order order : orders) 
	        {
	            // Lấy số lượng review và rating trung bình của từng đơn hàng
	            Double averageRating = reviewRepository.findAverageRatingForOrder(order.getId());

	            if (averageRating != null) {
	                totalSatisfaction += averageRating;
	                totalOrdersWithReviews++;  // Chỉ tính những đơn hàng có review
	            }
	        }

	        // Nếu có ít nhất một đơn hàng có review, tính mức độ hài lòng trung bình
	        if (totalOrdersWithReviews > 0) {
	            return totalSatisfaction / totalOrdersWithReviews;
	        }
	        return 0.0;  // Trả về 0 nếu không có review nào
	    }
}
