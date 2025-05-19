package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserUserId(Long userId);  // Lấy tất cả địa chỉ của user
    Address findByUserUserIdAndIsDefaultTrue(Long userId);  // Lấy địa chỉ mặc định của user
}
