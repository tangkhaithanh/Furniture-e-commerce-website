package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Cart;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CartRepository;
import vn.iotstar.repository.CartItemRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.CartService;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository; // Thêm UserRepository để lấy thông tin người dùng

    // Triển khai phương thức addToCart
    @Override
    public Cart addToCart(Long userId, Long productId, int quantity) {
        // Truy vấn người dùng từ UserRepository
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Người dùng không tồn tại.");
        }

        User user = userOpt.get();

        // Kiểm tra xem giỏ hàng của người dùng đã tồn tại chưa
        Cart cart = cartRepository.findByUserUserId(userId);

        if (cart == null) {
            // Nếu giỏ hàng không tồn tại, tạo mới giỏ hàng cho người dùng
            cart = new Cart();
            cart.setUser(user);  // Gán người dùng vào giỏ hàng
            cart = cartRepository.save(cart);
        }

        // Kiểm tra xem sản phẩm có tồn tại không
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại.");
        }

        Product product = productOpt.get();

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (cartItem != null) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới CartItem
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        // Lưu CartItem vào cơ sở dữ liệu
        cartItemRepository.save(cartItem);

        return cart;
    }
    
    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserUserId(userId);
    }
    
    @Override
    public void changeQuantity(Long cartItemId, int change) {
        // Lấy đối tượng CartItem từ database
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Cập nhật số lượng mới
        int newQuantity = cartItem.getQuantity() + change;
        if (newQuantity > 0) {
            cartItem.setQuantity(newQuantity);

            // Lưu lại thay đổi số lượng sản phẩm
            cartItemRepository.save(cartItem);
        }
    }
    
    @Override
 // Xóa sản phẩm khỏi giỏ hàng
    public void removeItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(cartItem);  // Xóa sản phẩm khỏi giỏ hàng
    }
    
    @Override
 // Phương thức để lấy CartItem theo itemId
    public CartItem getItemById(Long itemId) {
        return cartItemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Cart item not found"));
    }
    
    @Override
 // Phương thức để lưu giỏ hàng sau khi đã thay đổi
    public void save(Cart cart) {
        // Lưu lại các mục trong giỏ hàng
        for (CartItem item : cart.getCartItems()) {
            cartItemRepository.save(item);  // Lưu các mục trong giỏ hàng
        }
        // Lưu lại chính giỏ hàng
        cartRepository.save(cart);
    }
    
    @Override
    public Cart findCartByUser(User user) {
        return cartRepository.findByUser(user);
    }
    
    @Override
 // Tính tổng số lượng sản phẩm trong giỏ hàng
    public int getTotalCartItemCount(User user) {
        Cart cart = findCartByUser(user);
        if (cart == null || cart.getCartItems() == null) {
            return 0; // Nếu không có giỏ hàng hoặc không có sản phẩm, trả về 0
        }

        // Tính tổng số lượng sản phẩm trong giỏ hàng
        return cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)  // Lấy số lượng của từng CartItem
                .sum();  // Tính tổng số lượng
    }
    
    @Override
    public int getCartItemCount(Long userId) {
        Cart cart = cartRepository.findByUserUserId(userId);
        if (cart != null && cart.getCartItems() != null) {
            return cart.getCartItems().size();
        }
        return 0;
    }

}