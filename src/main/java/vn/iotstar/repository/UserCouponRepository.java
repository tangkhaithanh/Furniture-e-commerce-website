package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
}