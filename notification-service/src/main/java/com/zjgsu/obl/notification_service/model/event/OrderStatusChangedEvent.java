package com.zjgsu.obl.notification_service.model.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderStatusChangedEvent implements Serializable {
    private Long orderId;
    private Long userId;
    private String oldStatus;
    private String newStatus;

    public OrderStatusChangedEvent(Long orderId, Long userId, String oldStatus, String newStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}