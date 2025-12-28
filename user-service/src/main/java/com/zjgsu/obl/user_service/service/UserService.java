package com.zjgsu.obl.user_service.service;

import com.zjgsu.obl.user_service.common.JwtUtil;
import com.zjgsu.obl.user_service.dto.user.LoginRequest;
import com.zjgsu.obl.user_service.dto.user.LoginResponse;
import com.zjgsu.obl.user_service.dto.user.RegisterRequest;
import com.zjgsu.obl.user_service.dto.user.UserDTO;
import com.zjgsu.obl.user_service.model.User;
import com.zjgsu.obl.user_service.respository.UserRepository;
import com.zjgsu.obl.user_service.util.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest request;

    @Transactional
    public UserDTO register(RegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 加密存储        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return UserDTO.fromEntity(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userOpt.get();

        // 验证密码（使用加密比对）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if ("DISABLED".equals(user.getStatus())) {
            throw new RuntimeException("用户已被禁用");
        }

        // 生成Token - 传入userId
        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole(),
                user.getId()  // 传入userId
        );
        // 创建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(UserDTO.fromEntity(user));
        response.setMessage("登录成功");

        return response;
    }

    /**
     * 查询用户信息（网关已验证权限，这里只需要做最终检查）
     */
    public UserDTO getUserById(Long userId) {
        log.info("查询用户 - 请求者ID: {} (角色: {}), 目标用户ID: {}",
                currentUserId, currentUserRole, userId);

        // 查找目标用户
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 权限检查：只有管理员或用户自己可以查看信息
        if (!"ADMIN".equals(currentUserRole) && !currentUserId.equals(userId)) {
            throw new RuntimeException("无权查看其他用户信息");
        }

        return UserDTO.fromEntity(targetUser);
    }

    /**
     * 查询当前登录用户自己的信息
     */
    public UserDTO getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return UserDTO.fromEntity(user);
    }

    /**
     * 获取用户总数
     */
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * 获取指定时间范围内的新用户总数
     */
    public long countNewUsers(Date startDate, Date endDate) {
        return userRepository.countByCreatedAtBetween(startDate, endDate);
    }

    /**
     * 内部服务API：验证用户存在性
     */
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }
}