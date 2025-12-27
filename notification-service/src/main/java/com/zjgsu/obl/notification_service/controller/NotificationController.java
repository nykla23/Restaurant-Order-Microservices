package com.zjgsu.obl.notification_service.controller;
import com.zjgsu.obl.notification_service.common.ApiResponse;
import com.zjgsu.obl.notification_service.dto.notification.CreateNotificationRequest;
import com.zjgsu.obl.notification_service.dto.notification.NotificationCountDTO;
import com.zjgsu.obl.notification_service.dto.notification.NotificationDTO;
import com.zjgsu.obl.notification_service.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Validated
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取当前用户通知列表
     */
    @GetMapping
    public ApiResponse<List<NotificationDTO>> getNotifications() {
        Long userId = getCurrentUserId();
        log.info("获取用户通知列表，用户ID: {}", userId);

        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ApiResponse.success(notifications);
    }

    /**
     * 获取当前用户未读通知
     */
    @GetMapping("/unread")
    public ApiResponse<List<NotificationDTO>> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        log.info("获取用户未读通知，用户ID: {}", userId);

        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ApiResponse.success(notifications);
    }

    /**
     * 获取通知统计
     */
    @GetMapping("/count")
    public ApiResponse<NotificationCountDTO> getNotificationCount() {
        Long userId = getCurrentUserId();
        log.info("获取通知统计，用户ID: {}", userId);

        NotificationCountDTO count = notificationService.getNotificationCount(userId);
        return ApiResponse.success(count);
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId) {
        Long userId = getCurrentUserId();
        log.info("标记通知为已读，通知ID: {}, 用户ID: {}", notificationId, userId);

        notificationService.markAsRead(notificationId, userId);
        return ApiResponse.success("标记为已读成功", null);
    }

    /**
     * 标记所有通知为已读
     */
    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        log.info("标记所有通知为已读，用户ID: {}", userId);

        notificationService.markAllAsRead(userId);
        return ApiResponse.success("所有通知已标记为已读", null);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long notificationId) {
        Long userId = getCurrentUserId();
        log.info("删除通知，通知ID: {}, 用户ID: {}", notificationId, userId);

        notificationService.deleteNotification(notificationId, userId);
        return ApiResponse.success("删除通知成功", null);
    }

    /**
     * 创建通知（管理员用）
     */
    @PostMapping
    public ApiResponse<NotificationDTO> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        log.info("创建通知，标题: {}", request.getTitle());

        NotificationDTO notification = notificationService.createNotification(request);
        return ApiResponse.success("创建通知成功", notification);
    }

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId() {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            throw new RuntimeException("用户未登录");
        }
        return (Long) userIdObj;
    }
}