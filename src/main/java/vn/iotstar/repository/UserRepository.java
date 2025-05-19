package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.iotstar.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserId(Long userId);
	boolean existsByUsername(String username);
	Optional<User> findByUsername(String username);
	User findByResetPasswordToken(String token);
	User findByEmail(String email);
	/*
	 * @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'USER'") long
	 * countUsersByRole();
	 */
	
	Page<User> findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(String email, String username, Pageable pageable);
}
