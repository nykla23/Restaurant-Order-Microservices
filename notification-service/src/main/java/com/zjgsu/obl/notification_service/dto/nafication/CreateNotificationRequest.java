package com.zjgsu.obl.notification_service.dto.nafication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class CreateNotificationRequest {

    private Long userId; // null表示广播通知

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "类型不能为空")
    private String type; // ORDER, SYSTEM, INVENTORY, PROMOTION

    private String relatedType; // ORDER, DISH

    private Long relatedId;

    private String priority = "NORMAL"; // LOW, NORMAL, HIGH, URGENT

    private Date expiresAt; // 过期时间
}