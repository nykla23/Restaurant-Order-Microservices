package com.zjgsu.obl.notification_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // 接收用户ID，null表示广播通知

    @Column(nullable = false, length = 100)
    private String title; // 通知标题

    @Column(columnDefinition = "TEXT")
    private String content; // 通知内容

    @Column(length = 50)
    private String type; // 通知类型：ORDER-订单，SYSTEM-系统，INVENTORY-库存，PROMOTION-促销

    @Column(length = 50)
    private String relatedType; // 关联类型：ORDER-订单，DISH-菜品

    private Long relatedId; // 关联ID

    @Column(length = 20)
    private String status = "UNREAD"; // 状态：UNREAD-未读，READ-已读

    @Column(nullable = false, length = 20)
    private String priority = "NORMAL"; // 优先级：LOW-低，NORMAL-中，HIGH-高，URGENT-紧急

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "read_at")
    private Date readAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at")
    private Date expiresAt; // 过期时间，null表示永不过期

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}