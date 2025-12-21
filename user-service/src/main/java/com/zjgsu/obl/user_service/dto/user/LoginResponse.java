package com.zjgsu.obl.user_service.dto.user;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserDTO user;
    private String message;
}