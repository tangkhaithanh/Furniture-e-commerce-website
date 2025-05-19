package vn.iotstar.services;

import vn.iotstar.entity.Coupon;
import vn.iotstar.entity.UserCoupon;

public interface CouponService {

	Coupon findByCode(String code);

	Coupon saveCoupon(Coupon Coupon);

}
