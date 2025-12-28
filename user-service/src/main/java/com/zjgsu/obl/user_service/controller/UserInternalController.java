package com.zjgsu.obl.user_service.controller;

import com.zjgsu.obl.user_service.common.ApiResponse;
import com.zjgsu.obl.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/internal/users")
@Slf4j
public class UserInternalController {

    @Autowired
    private UserService userService;

    @Value("${service.auth.internal-key}")
    private String internalApiKey;

    /**
     * 内部服务API：验证用户存在性
     * 使用服务间认证（简单API Key验证）
     */
    @GetMapping("/exists/{userId}")
    public ApiResponse<Boolean> checkUserExists(
            @PathVariable Long userId,
            @RequestHeader(value = "X-Service-Key", required = false) String serviceKey) {

        // 简单服务间认证（实际生产环境使用更安全的机制）
        if (!"INTERNAL-SERVICE-KEY".equals(serviceKey)) {
            throw new RuntimeException("服务间认证失败");
        }

        log.debug("内部验证用户存在性: {}", userId);
        boolean exists = userService.userExists(userId);
        return ApiResponse.success(exists);
    }

    /**
     * 获取用户总数（内部API）
     */
    @GetMapping("/count")
    public ApiResponse<Long> getTotalUsers(
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey) {

        // 简单的服务间认证
        if (!validateInternalKey(internalKey)) {
            throw new RuntimeException("内部服务认证失败");
        }

        log.debug("内部调用：获取用户总数");
        long count = userService.countUsers();
        return ApiResponse.success(count);
    }

    /**
     * 获取时间范围内新增用户数（内部API）
     */
    @GetMapping("/count/new")
    public ApiResponse<Long> countNewUsers(
            @RequestParam Date startDate,
            @RequestParam Date endDate,
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey) {

        if (!validateInternalKey(internalKey)) {
            throw new RuntimeException("内部服务认证失败");
        }

        log.debug("内部调用：获取新增用户数，{} - {}", startDate, endDate);
        long count = userService.countNewUsers(startDate, endDate);
        return ApiResponse.success(count);
    }

    private boolean validateInternalKey(String key) {
        return internalApiKey != null && internalApiKey.equals(key);
    }
}