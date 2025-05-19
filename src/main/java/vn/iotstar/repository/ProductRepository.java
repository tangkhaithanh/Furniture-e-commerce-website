package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findAll(Pageable pageable);
	Page<Product> findByProductStatus(int productStatus, Pageable pageable);
   
    
    @Query("SELECT p FROM Product p WHERE p.productStatus = :status")
    Page<Product> findAllByProductStatus(@Param("status") int status, Pageable pageable);

	@Query("SELECT p, " +
			"(SELECT AVG(r.rating) FROM p.reviews r WHERE r.product.id = p.id) AS averageRating, " +
			"(SELECT COALESCE(SUM(oi.quantity), 0) FROM p.orderItems oi WHERE oi.product.id = p.id) AS totalSold " +
			"FROM Product p " +
			"WHERE p.category.id = :categoryId " +
			"AND p.status = :status " +
			"AND p.productStatus = 1")
	Page<Object[]> findByCategoryIdAndStatusWithAvgRating(Long categoryId, int status, Pageable pageable);



	@Query("SELECT p FROM Product p " +
			"JOIN p.orderItems oi ON oi.product.id = p.id " +
			"WHERE p.productStatus = 1 " +
			"GROUP BY p.id ORDER BY SUM(oi.quantity) DESC")
	List<Product> findBestSellingProducts();


	@Query("SELECT p, " +
			"(SELECT AVG(r.rating) FROM p.reviews r WHERE r.product.id = p.id) AS averageRating, " +
			"(SELECT COALESCE(SUM(oi.quantity), 0) FROM p.orderItems oi WHERE oi.product.id = p.id) AS totalSold " +
			"FROM Product p " +
			"WHERE p.category.id = :categoryId " +
			"AND p.status = :status " +
			"AND p.productStatus = 1 " +
			"AND p.name LIKE %:keyword%")
	Page<Object[]> findByCategoryIdAndStatusWithAvgRatingAndKeyword(Long categoryId, String keyword, int status, Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
	
	Page<Product> findById(Long id, Pageable pageable);
}