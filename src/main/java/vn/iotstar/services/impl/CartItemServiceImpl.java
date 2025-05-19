package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.services.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
    private CartItemRepository cartItemRepository;  // Tiêm repository vào service

	@Override
    public void deleteCardItem(Long id) {
        cartItemRepository.deleteById(id);  // Sử dụng phương thức deleteById của JpaRepository
    }
}
