package com.zjgsu.obl.order_service.event;
import com.zjgsu.obl.order_service.model.event.InventoryWarningEvent;
import com.zjgsu.obl.order_service.model.event.OrderCreatedEvent;
import com.zjgsu.obl.order_service.model.event.OrderStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发布订单创建事件
     */
    public void publishOrderCreated(Long orderId, String orderNumber, Long userId) {
        log.info("发布订单创建事件，订单ID: {}", orderId);
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, orderNumber, userId);
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * 发布订单状态变更事件
     */
    public void publishOrderStatusChanged(Long orderId, Long userId, String oldStatus, String newStatus) {
        log.info("发布订单状态变更事件，订单ID: {}，状态: {} -> {}", orderId, oldStatus, newStatus);
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(orderId, userId, oldStatus, newStatus);
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * 发布库存预警事件
     */
    public void publishInventoryWarning(Long dishId, String dishName, Integer currentStock) {
        log.info("发布库存预警事件，菜品ID: {}，库存: {}", dishId, currentStock);
        InventoryWarningEvent event = new InventoryWarningEvent(dishId, dishName, currentStock);
        applicationEventPublisher.publishEvent(event);
    }
}