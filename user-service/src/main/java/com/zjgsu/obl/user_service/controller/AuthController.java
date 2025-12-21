package com.zjgsu.obl.user_service.controller;


import com.zjgsu.obl.user_service.common.ApiResponse;
import com.zjgsu.obl.user_service.dto.user.LoginRequest;
import com.zjgsu.obl.user_service.dto.user.LoginResponse;
import com.zjgsu.obl.user_service.dto.user.RegisterRequest;
import com.zjgsu.obl.user_service.dto.user.UserDTO;
import com.zjgsu.obl.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("注册请求: {}", request.getUsername());
        UserDTO userDTO = userService.register(request);
        return ApiResponse.success("注册成功", userDTO);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("登录请求: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return ApiResponse.success(response);
    }
}