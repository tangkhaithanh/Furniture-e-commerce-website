package vn.iotstar.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Orders")
public class Order implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;
	
	@Column(name = "status", nullable = false, length = 50)
	private String status;
	
	@Column(name = "payment_method")
    private String paymentMethod;

	@Column(name = "image_url", columnDefinition = "varchar(255)")
	private String imageUrl;

	 @CreationTimestamp
	 @Column(name = "created_at", nullable = false, updatable = false)
	 private LocalDateTime createdAt;
	 
	 @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	 private List<OrderItem> orderItems;
	 
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "coupon_id")
	 private Coupon coupon;
	 
	 @OneToOne(mappedBy = "order")
	 private ReturnRequest returnRequest;

}