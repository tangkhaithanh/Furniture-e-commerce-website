package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByProductId(Long orderId);
	
	// Tính trung bình rating cho một đơn hàng cụ thể
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id IN (SELECT oi.product.id FROM OrderItem oi WHERE oi.order.id = :orderId)")
    Double findAverageRatingForOrder(Long orderId);

    // Tính tổng số review cho một đơn hàng cụ thể
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id IN (SELECT oi.product.id FROM OrderItem oi WHERE oi.order.id = :orderId)")
    long countReviewsForOrder(Long orderId);
    
    Review findByUserAndProduct(User user, Product product);
    boolean existsByUserUserIdAndProductId(Long userId, Long productId);
    Review findByUserUserIdAndProductId(Long userId, Long productId);
}
