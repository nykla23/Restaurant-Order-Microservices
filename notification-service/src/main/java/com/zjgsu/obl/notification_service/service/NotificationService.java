package com.zjgsu.obl.notification_service.service;

import com.zjgsu.obl.notification_service.client.DishClient;
import com.zjgsu.obl.notification_service.client.OrderClient;
import com.zjgsu.obl.notification_service.dto.DishDTO;
import com.zjgsu.obl.notification_service.dto.OrderDTO;
import com.zjgsu.obl.notification_service.dto.notification.CreateNotificationRequest;
import com.zjgsu.obl.notification_service.dto.notification.NotificationCountDTO;
import com.zjgsu.obl.notification_service.dto.notification.NotificationDTO;
import com.zjgsu.obl.notification_service.model.Notification;
import com.zjgsu.obl.notification_service.model.event.InventoryWarningEvent;
import com.zjgsu.obl.notification_service.model.event.OrderCreatedEvent;
import com.zjgsu.obl.notification_service.model.event.OrderStatusChangedEvent;
import com.zjgsu.obl.notification_service.respository.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private HttpServletRequest request;

//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private DishService dishService;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private DishClient dishClient;


    /**
     * 监听订单创建事件
     */
    @EventListener
    @Async  // 异步处理，避免阻塞主线程
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("处理订单创建事件，订单ID: {}", event.getOrderId());

        // 1. 给用户发送订单创建通知
        createOrderStatusNotification(event.getOrderId(), event.getUserId(), null, "PENDING");

        // 2. 给后厨发送新订单通知
        createNewOrderNotification(event.getOrderId(), event.getOrderNumber());
    }

    /**
     * 监听订单状态变更事件
     */
    @EventListener
    @Async
    @Transactional
    public void handleOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        log.info("处理订单状态变更事件，订单ID: {}，状态: {} -> {}",
                event.getOrderId(), event.getOldStatus(), event.getNewStatus());

        createOrderStatusNotification(
                event.getOrderId(), event.getUserId(), event.getOldStatus(), event.getNewStatus());
    }

    /**
     * 监听库存预警事件
     */
    @EventListener
    @Async
    @Transactional
    public void handleInventoryWarningEvent(InventoryWarningEvent event) {
        log.info("处理库存预警事件，菜品ID: {}，库存: {}", event.getDishId(), event.getCurrentStock());

        createInventoryWarningNotification(
                event.getDishId(), event.getDishName(), event.getCurrentStock());
    }


    /**
     * 创建通知
     */
    @Transactional
    public NotificationDTO createNotification(CreateNotificationRequest request) {
        log.info("创建通知，标题: {}", request.getTitle());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setType(request.getType());
        notification.setRelatedType(request.getRelatedType());
        notification.setRelatedId(request.getRelatedId());
        notification.setPriority(request.getPriority());
        notification.setExpiresAt(request.getExpiresAt());

        Notification savedNotification = notificationRepository.save(notification);
        log.info("通知创建成功，ID: {}", savedNotification.getId());

        return NotificationDTO.fromEntity(savedNotification);
    }

    /**
     * 创建订单状态变更通知
     */
    @Transactional
    public void createOrderStatusNotification(Long orderId, Long userId, String oldStatus, String newStatus) {
        String title = "订单状态更新";
        String oldStatusText = translateStatus(oldStatus);
        String newStatusText = translateStatus(newStatus);

        String content;
        if (oldStatus == null) {
            content = String.format("您的订单 #%d 已创建，状态: %s", orderId, newStatusText);
        } else {
            content = String.format("您的订单 #%d 状态已从 %s 变更为 %s", orderId, oldStatusText, newStatusText);
        }

        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(userId);
        request.setTitle(title);
        request.setContent(content);
        request.setType("ORDER");
        request.setRelatedType("ORDER");
        request.setRelatedId(orderId);

        // 如果是重要状态变更，提高优先级
        if ("CANCELLED".equals(newStatus) || "COMPLETED".equals(newStatus)) {
            request.setPriority("HIGH");
        }

        createNotification(request);
        log.info("创建订单状态通知，订单ID: {}, 用户ID: {}", orderId, userId);
    }

    /**
     * 创建库存预警通知
     */
    @Transactional
    public void createInventoryWarningNotification(Long dishId, String dishName, Integer currentStock) {
        String title = "库存预警";
        String content = String.format("菜品 '%s' 库存不足，当前库存: %d", dishName, currentStock);

        // 给管理员发送通知（userId为null表示广播给所有用户，这里我们假设有管理员用户）
        // 实际项目中应该查询管理员用户ID
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(null); // 广播通知
        request.setTitle(title);
        request.setContent(content);
        request.setType("INVENTORY");
        request.setRelatedType("DISH");
        request.setRelatedId(dishId);
        request.setPriority("URGENT");

        createNotification(request);
        log.info("创建库存预警通知，菜品ID: {}, 菜品名: {}", dishId, dishName);
    }

    /**
     * 创建新订单通知（给后厨）
     */
    @Transactional
    public void createNewOrderNotification(Long orderId, String orderNumber) {
        String title = "新订单通知";
        String content = String.format("有新订单 #%s 等待处理", orderNumber);

        // 这里应该查询后厨用户的ID，暂时使用广播
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(null); // 广播给后厨
        request.setTitle(title);
        request.setContent(content);
        request.setType("ORDER");
        request.setRelatedType("ORDER");
        request.setRelatedId(orderId);
        request.setPriority("HIGH");

        createNotification(request);
        log.info("创建新订单通知，订单ID: {}, 订单号: {}", orderId, orderNumber);
    }

    /**
     * 获取用户通知列表
     */
    public List<NotificationDTO> getUserNotifications(Long userId) {
        log.info("获取用户通知列表，用户ID: {}", userId);

        Date now = new Date();
        List<Notification> notifications = notificationRepository.findValidByUserId(userId, now);

        return notifications.stream()
                .map(this::enrichNotification)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户未读通知
     */
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        log.info("获取用户未读通知，用户ID: {}", userId);

        List<Notification> notifications = notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "UNREAD");

        return notifications.stream()
                .map(this::enrichNotification)
                .collect(Collectors.toList());
    }

    /**
     * 获取通知统计
     */
    public NotificationCountDTO getNotificationCount(Long userId) {
        NotificationCountDTO countDTO = new NotificationCountDTO();

        Date now = new Date();
        List<Notification> allNotifications = notificationRepository.findValidByUserId(userId, now);

        countDTO.setTotalCount((long) allNotifications.size());
        countDTO.setUnreadCount(notificationRepository.countUnreadByUserId(userId));
        countDTO.setUrgentCount(notificationRepository.countUrgentUnreadByUserId(userId));
        countDTO.setOrderCount(notificationRepository.countUnreadByUserIdAndType(userId, "ORDER"));
        countDTO.setInventoryCount(notificationRepository.countUnreadByUserIdAndType(userId, "INVENTORY"));

        return countDTO;
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId, new Date());

        if (updated > 0) {
            log.info("标记通知为已读，通知ID: {}, 用户ID: {}", notificationId, userId);
        } else {
            log.warn("标记通知失败，通知ID: {}, 用户ID: {}", notificationId, userId);
        }
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        int updated = notificationRepository.markAllAsRead(userId, new Date());

        if (updated > 0) {
            log.info("标记所有通知为已读，用户ID: {}, 数量: {}", userId, updated);
        } else {
            log.info("没有需要标记为已读的通知，用户ID: {}", userId);
        }
    }

    /**
     * 删除通知
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        // 检查权限：用户只能删除自己的通知
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此通知");
        }

        notificationRepository.delete(notification);
        log.info("删除通知，通知ID: {}, 用户ID: {}", notificationId, userId);
    }

    /**
     * 定期清理过期通知
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    @Transactional
    public void cleanExpiredNotifications() {
        Date now = new Date();
        int deleted = notificationRepository.deleteExpired(now);

        if (deleted > 0) {
            log.info("清理过期通知，数量: {}", deleted);
        }
    }

    /**
     * 丰富通知信息（添加额外信息）
     */
    private NotificationDTO enrichNotification(Notification notification) {
        NotificationDTO dto = NotificationDTO.fromEntity(notification);

        // 根据通知类型添加额外信息
        if ("ORDER".equals(notification.getType()) && "ORDER".equals(notification.getRelatedType())) {
            try {
                OrderDTO order = orderClient.getOrderById(notification.getRelatedId());
                dto.setExtraInfo(new OrderExtraInfo(order.getOrderNumber(), order.getStatus()));
            } catch (Exception e) {
                log.warn("获取订单额外信息失败，订单ID: {}", notification.getRelatedId(), e);
            }
        } else if ("INVENTORY".equals(notification.getType()) && "DISH".equals(notification.getRelatedType())) {
            try {
                DishDTO dish = dishClient.getDishById(notification.getRelatedId());
                dto.setExtraInfo(new DishExtraInfo(dish.getName(), dish.getStock()));
            } catch (Exception e) {
                log.warn("获取菜品额外信息失败，菜品ID: {}", notification.getRelatedId(), e);
            }
        }

        return dto;
    }

    /**
     * 翻译订单状态
     */
    private String translateStatus(String status) {
        if (status == null) return "未知";

        switch (status) {
            case "PENDING": return "待确认";
            case "CONFIRMED": return "已确认";
            case "PREPARING": return "制作中";
            case "READY": return "已出餐";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }

    // 内部类：订单额外信息
    @Data
    private static class OrderExtraInfo {
        private String orderNumber;
        private String orderStatus;

        public OrderExtraInfo(String orderNumber, String orderStatus) {
            this.orderNumber = orderNumber;
            this.orderStatus = orderStatus;
        }
    }

    // 内部类：菜品额外信息
    @Data
    private static class DishExtraInfo {
        private String dishName;
        private Integer stock;

        public DishExtraInfo(String dishName, Integer stock) {
            this.dishName = dishName;
            this.stock = stock;
        }
    }
}