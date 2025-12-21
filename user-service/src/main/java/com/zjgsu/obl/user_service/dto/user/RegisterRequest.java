package com.zjgsu.obl.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String phone;
    private String email;
    private String nickname;
    private String role = "CUSTOMER";
}