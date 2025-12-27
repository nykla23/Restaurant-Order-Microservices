package com.zjgsu.obl.notification_service.dto.notification;

import lombok.Data;

@Data
public class NotificationCountDTO {
    private Long totalCount;      // 总通知数
    private Long unreadCount;     // 未读通知数
    private Long urgentCount;     // 紧急通知数
    private Long orderCount;      // 订单相关通知数
    private Long inventoryCount;  // 库存相关通知数
}