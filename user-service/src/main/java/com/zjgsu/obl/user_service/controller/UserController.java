package com.zjgsu.obl.user_service.controller;

import com.zjgsu.obl.user_service.common.ApiResponse;
import com.zjgsu.obl.user_service.dto.user.UserDTO;
import com.zjgsu.obl.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询指定用户（需要权限：管理员或用户自己）
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserDTO> getUser(@PathVariable Long userId) {
        log.info("查询用户: {}", userId);
        UserDTO userDTO = userService.getUserById(userId);
        return ApiResponse.success(userDTO);
    }

    /**
     * 查询当前登录用户自己的信息
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser() {
        log.info("查询当前用户");
        UserDTO userDTO = userService.getCurrentUser();
        return ApiResponse.success(userDTO);
    }

    /**
     * 获取用户总数
     */
    @GetMapping("/count")
    public ApiResponse<Long> countUsers() {
        log.info("获取用户总数");
        long count = userService.countUsers();
        return ApiResponse.success(count);
    }

    /**
     * 获取指定时间范围内的新用户总数
     */
    @GetMapping("/count/new")
    public ApiResponse<Long> countNewUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
        log.info("获取指定时间范围内的新用户总数: {} - {}", startDate, endDate);
        long count = userService.countNewUsers(startDate, endDate);
        return ApiResponse.success(count);
    }

}