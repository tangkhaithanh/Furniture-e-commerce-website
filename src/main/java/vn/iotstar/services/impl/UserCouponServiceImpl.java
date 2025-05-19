package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.UserCoupon;
import vn.iotstar.repository.UserCouponRepository;
import vn.iotstar.services.UserCouponService;

@Service
public class UserCouponServiceImpl implements UserCouponService {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Override
    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        return userCouponRepository.save(userCoupon);
    }
}