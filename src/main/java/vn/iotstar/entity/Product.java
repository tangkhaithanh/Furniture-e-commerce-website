package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Products")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;

    
    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "image_url", columnDefinition = "varchar(255)")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
  
    // Thời gian tạo sản phẩm :
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;  // Danh sách các OrderItem liên quan đến Product này
    
 // Thêm quan hệ OneToMany với Reviews
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;  // Một sản phẩm có thể có nhiều đánh giá
    
 // Quan hệ OneToMany với ProductLike (một sản phẩm có thể có nhiều like)
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductLike> productLikes;  // Một sản phẩm có thể có nhiều lượt like
	/*
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "vendor_id", nullable = false) private User vendor;
	 */
 // Thêm thuộc tính product_status để kiểm tra trạng thái phê duyệt
    @Column(name = "product_status", nullable = false)
    private int productStatus; // 0 - chưa phê duyệt, 1 - đã phê duyệt
}	