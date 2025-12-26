package com.zjgsu.obl.notification_service.dto.nafication;
import com.zjgsu.obl.notification_service.model.Notification;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private String relatedType;
    private Long relatedId;
    private String status;
    private String priority;
    private Date createdAt;
    private Date readAt;
    private Date expiresAt;

    // 扩展信息
    private Object extraInfo; // 根据类型存储额外信息

    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setType(notification.getType());
        dto.setRelatedType(notification.getRelatedType());
        dto.setRelatedId(notification.getRelatedId());
        dto.setStatus(notification.getStatus());
        dto.setPriority(notification.getPriority());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());
        dto.setExpiresAt(notification.getExpiresAt());
        return dto;
    }
}