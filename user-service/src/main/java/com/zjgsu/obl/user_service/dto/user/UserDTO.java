package com.zjgsu.obl.user_service.dto.user;

import com.zjgsu.obl.user_service.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;
    private String role;
    private String status;
    private Date createdAt;

    // 静态工厂方法：从 User 实体创建 DTO
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}