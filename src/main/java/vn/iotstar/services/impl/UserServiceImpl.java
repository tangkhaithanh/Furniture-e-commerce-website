package vn.iotstar.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.exceptions.CustomerNotFoundException;
import vn.iotstar.exceptions.DataNotFoundException;
import vn.iotstar.exceptions.PermissionDenyException;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.services.UserService;
import vn.iotstar.utils.JwtTokenUtil;
//import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;


    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        //register user
        String username = userDTO.getUsername();
        // Kiểm tra xem số điện thoại đã tồn tại hay chưa
        if(userRepository.existsByUsername(username)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role =roleRepository.findById(2L)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
//        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
//            throw new PermissionDenyException("You cannot register an admin account");
//        }
        //convert from userDTO => userEntity
        User newUser = User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();
        newUser.setRole(role);
        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);
        return userRepository.save(newUser);
    }
    @Override
    public String login(String username, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("Invalid phone number / password");
        }
        //return optionalUser.get();//muốn trả JWT token ?
        User existingUser = optionalUser.get();
        //check password
        if(!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong phone number or password");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password,
                existingUser.getAuthorities()
        );
//        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException{
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    @Override
    public User getUser(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
	public Page<User> getUsers(int pageNum) {
		Pageable pageable = PageRequest.of(pageNum, 10); 
        return userRepository.findAll(pageable);
	}
    
    @Override
	public User saveUser(User user) {
		return userRepository.save(user);
		
	}
    
    @Override
	public Optional<User> getUserById(Long id) {
		 return userRepository.findById(id);
	}
    
    @Override
	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
		
	}

	@Override
	public Page<User> searchUsers(String keyword, int page) {
	    Pageable pageable = PageRequest.of(page, 10);
	    return userRepository.findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(keyword, keyword, pageable);
	}
}

