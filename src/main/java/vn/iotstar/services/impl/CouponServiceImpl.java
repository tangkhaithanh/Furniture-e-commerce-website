package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Coupon;
import vn.iotstar.entity.UserCoupon;
import vn.iotstar.repository.CouponRepository;
import vn.iotstar.services.CouponService;

@Service
public class CouponServiceImpl implements  CouponService{
	@Autowired
    private CouponRepository couponRepository;
	
	@Override
    public Coupon findByCode(String code) {
        return couponRepository.findByCode(code);
    }

	@Override
	 public Coupon saveCoupon(Coupon coupon) {
		return couponRepository.save(coupon);
		
	}
}
