package vn.iotstar.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>
{
	 Page<Order> findByStatusAndUserUserId(String status, Long userId, Pageable pageable);

	 Page<Order> findByStatusInAndUserUserId(List<String> statuses, Long userId, Pageable pageable);

	 Page<Order> findByUserUserId(Long userId, Pageable pageable);
	 
	 Page<Order> findByUserUserIdAndStatusNot(Long userId, String status, Pageable pageable);
	 
	 List<Order> findByStatus(String status);
	 
	 Order findOrderById(Long id);
	 
	 List<Order> findByIdInAndStatus(List<Long> orderIds, String status);
	 
	// Lấy tổng số đơn hàng
	 long countByStatus(String status);
	 
	 //Lấy tổng doanh thu
	 @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'Đã giao'")
	 BigDecimal sumTotalRevenue();
	 
	 @Query("SELECT o FROM Order o JOIN o.user u WHERE " +
	           "(:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
	           "OR CAST(o.id AS string) LIKE CONCAT('%', :search, '%')) " +
	           "AND o.status = :status")
	    Page<Order> findBySearchCriteria(@Param("search") String search, 
	                                   @Param("status") String status, 
	                                   Pageable pageable);

	   // Thêm phương thức đếm số đơn hàng theo trạng thái
	   @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status") 
	   long countByStatusAndHavePage(@Param("status") String status);

	   // Thêm phương thức lấy tổng doanh thu theo trạng thái
	   @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
	   BigDecimal sumTotalAmountByStatus(@Param("status") String status);

	   // Thêm phương thức lấy đơn hàng mới nhất
	   @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
	   List<Order> findLatestOrders(Pageable pageable);

	   @Query("SELECT o FROM Order o JOIN o.user u WHERE " +
	           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
	           "OR CAST(o.id AS string) LIKE CONCAT('%', :search, '%')")
	    Page<Order> findBySearchOnly(@Param("search") String search, Pageable pageable);
	   
	   Page<Order> findByStatus(String status, Pageable pageable);
	   
	   @Query("SELECT o FROM Order o WHERE o.id = :id OR LOWER(o.user.username) LIKE LOWER(CONCAT('%', :username, '%'))")
	   Page<Order> searchOrders(@Param("id") Long id, @Param("username") String username, Pageable pageable);
	   
	   List<Order> findByCreatedAtBetweenAndStatus(
		        LocalDateTime startDate, 
		        LocalDateTime endDate, 
		        String status
		    );
}