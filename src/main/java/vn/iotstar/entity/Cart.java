package vn.iotstar.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Carts")
public class Cart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;
	
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(fetch = FetchType.LAZY)  // Thêm OneToOne để mỗi User chỉ có một Cart
    @JoinColumn(name = "user_id", nullable = false, unique = true)  // Ràng buộc User chỉ có 1 Cart
    private User user;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();
	 
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)  // Quan hệ 1-nhiều với CartItem
	private List<CartItem> cartItems;  // Một Cart có thể có nhiều CartItem

}