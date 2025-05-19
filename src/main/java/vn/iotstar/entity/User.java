package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Users")
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, columnDefinition = "nvarchar(255)")
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(255)")
    private String password;

    @Column(name = "email", nullable = false, columnDefinition = "nvarchar(255)")
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "reset_password_token")
    private String resetPasswordToken;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)  // Một User có thể có nhiều Review
    private List<Review> reviews;  // Danh sách các Review mà người dùng đã viết
    
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)  // Một User có thể thích nhiều sản phẩm
    private List<ProductLike> productLikes;  // Danh sách các sản phẩm mà người dùng đã thích
    
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private Cart cart;
    
 // Mối quan hệ One-to-Many với Address (mỗi user có thể có nhiều địa chỉ)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Address> addresses;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;	
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserCoupon> userCoupons;
    
    @Column(name = "avatar_url", columnDefinition = "nvarchar(500)")
    private String avatarUrl;

    @Column(name = "phone_number", columnDefinition = "nvarchar(20)")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Enum cho giới tính
    public enum Gender {
        MALE, 
        FEMALE, 
        OTHER
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_"+getRole().getName().toUpperCase()));
//        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorityList;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}