package vn.iotstar.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.entity.User;
import vn.iotstar.repository.ReviewRepository;
import vn.iotstar.services.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService{
	@Autowired
    private ReviewRepository reviewRepository;
	
	@Override
	public List<Review> findReviewsByOrderId(Long orderId) {
        return reviewRepository.findByProductId(orderId);
    }
	
	@Override
	public void saveReview(Review review) {
        reviewRepository.save(review);
    }
	
	@Override
    public Review findByUserAndProduct(User user, Product product) {
        return reviewRepository.findByUserAndProduct(user, product);
    }
	
	@Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserUserIdAndProductId(userId, productId);
    }
	
	 @Override
	    public Review findByUserIdAndProductId(Long userId, Long productId) {
	        return reviewRepository.findByUserUserIdAndProductId(userId, productId);
	    }

}
