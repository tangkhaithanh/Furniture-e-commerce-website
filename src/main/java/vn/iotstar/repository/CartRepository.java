package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUserUserId(Long userId);

	Cart findByUser(User user);
}
