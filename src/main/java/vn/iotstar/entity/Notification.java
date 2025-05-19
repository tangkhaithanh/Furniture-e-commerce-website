package vn.iotstar.entity;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Liên kết với shipper (User)
    @ToString.Exclude  // Thêm vào
    @EqualsAndHashCode.Exclude  // Thêm vào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Liên kết với đơn hàng (Order)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    // Thời gian tạo thông báo
    
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(nullable = false)
    private java.util.Date timestamp;
    
    // Nội dung thông báo (Kiểu TEXT)
    @Lob
    @Column(nullable = false)
    private String message;
    
    // Trạng thái đã đọc
    @Column(nullable = false)
    private boolean isRead = false;
    
    @Column(nullable = false,name = "status", columnDefinition ="nvarchar(255)")
    private String status;
}