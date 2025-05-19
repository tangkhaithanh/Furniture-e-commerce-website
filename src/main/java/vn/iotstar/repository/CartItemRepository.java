package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndProductId(Long cartId, Long productId);
    // T?o truy v?n d? t�m c�c CartItem c?a ngu?i d�ng d?a tr�n danh s�ch s?n ph?m du?c ch?n
    List<CartItem> findByIdInAndCartUserUserId(List<Long> productIds, Long userId);  
}