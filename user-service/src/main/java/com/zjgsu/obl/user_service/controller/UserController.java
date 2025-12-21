package com.zjgsu.obl.user_service.controller;

import com.zjgsu.obl.user_service.common.ApiResponse;
import com.zjgsu.obl.user_service.dto.user.UserDTO;
import com.zjgsu.obl.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}