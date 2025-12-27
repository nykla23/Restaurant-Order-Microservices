package com.zjgsu.obl.notification_service.model.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderCreatedEvent implements Serializable {
    private Long orderId;
    private String orderNumber;
    private Long userId;

    public OrderCreatedEvent(Long orderId, String orderNumber, Long userId) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
    }
}