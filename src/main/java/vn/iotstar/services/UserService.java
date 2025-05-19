package vn.iotstar.services;


import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.User;

import java.util.Optional;

import org.springframework.data.domain.Page;

public interface UserService {
	User findById(Long userId);
	User createUser(UserDTO userDTO) throws Exception;
	String login(String phoneNumber, String password) throws Exception;
	void updateResetPasswordToken(String token, String email);
	User getUser(String token);
	void updatePassword(User user, String newPassword);
	User getUserByEmail(String email);
	Optional<User> getUserByUsername(String username);
	Page<User> getUsers(int pageNum);
	User saveUser(User user);
	Optional<User> getUserById(Long id);
	void deleteUserById(Long id);
	Page<User> searchUsers(String keyword, int page);
}
