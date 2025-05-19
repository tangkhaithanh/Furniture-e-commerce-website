package vn.iotstar.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.dto.RevenueDTO;
import vn.iotstar.entity.Order;
import vn.iotstar.repository.OrderRepository;
import vn.iotstar.services.RevenueService;
@Service
public class RevenueServiceImpl  implements RevenueService{

	@Autowired
	private OrderRepository orderRepository;
	@Override
	public RevenueDTO getRevenueData(LocalDate startDate, LocalDate endDate) 
	{
		 LocalDateTime startDateTime = startDate.atStartOfDay();
	     LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

	        // Fetch orders within date range
	        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatus(
	            startDateTime, 
	            endDateTime,
	            "Đã giao" // Only count completed orders
	        );

	        // Create date list for all days in range
	        List<LocalDate> dateRange = startDate.datesUntil(endDate.plusDays(1))
	            .collect(Collectors.toList());

	        // Initialize revenue map with zero values for all dates
	        Map<LocalDate, BigDecimal> dailyRevenue = dateRange.stream()
	            .collect(Collectors.toMap(
	                date -> date,
	                date -> BigDecimal.ZERO
	            ));

	        // Calculate daily revenue
	        orders.forEach(order -> {
	            LocalDate orderDate = order.getCreatedAt().toLocalDate();
	            dailyRevenue.merge(
	                orderDate,
	                order.getTotalAmount(),
	                BigDecimal::add
	            );
	        });

	        // Create DTO
	        RevenueDTO revenueDTO = new RevenueDTO();
	        
	        // Format dates and get revenues in order
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        revenueDTO.setDates(dateRange.stream()
	            .map(formatter::format)
	            .collect(Collectors.toList()));
	            
	        revenueDTO.setRevenues(dateRange.stream()
	            .map(dailyRevenue::get)
	            .collect(Collectors.toList()));

	        // Calculate total revenue
	        BigDecimal totalRevenue = orders.stream()
	            .map(Order::getTotalAmount)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	        revenueDTO.setTotalRevenue(totalRevenue);

	        // Set total orders count
	        revenueDTO.setTotalOrders((long) orders.size());

	        // Calculate average order value
	        revenueDTO.setAvgOrderValue(orders.isEmpty() ? 
	            BigDecimal.ZERO : 
	            totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP));
	        return revenueDTO;
	}
}
