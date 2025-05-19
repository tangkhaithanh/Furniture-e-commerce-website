package vn.iotstar.services;

import java.util.List;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;

public interface ReviewService {

	void saveReview(Review review);

	List<Review> findReviewsByOrderId(Long orderId);

	Review findByUserAndProduct(User user, Product product);
	
	 boolean hasUserReviewedProduct(Long userId, Long productId);

	Review findByUserIdAndProductId(Long userId, Long productId);
}
