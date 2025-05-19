package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductLike;
import vn.iotstar.entity.User;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long>{
	 ProductLike findByProductAndUser(Product product, User user);
}
