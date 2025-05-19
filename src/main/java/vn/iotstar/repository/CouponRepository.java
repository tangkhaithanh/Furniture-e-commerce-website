package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Coupon findByCode(String code);
}
