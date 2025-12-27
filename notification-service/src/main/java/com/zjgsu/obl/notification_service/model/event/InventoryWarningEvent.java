package com.zjgsu.obl.notification_service.model.event;

import lombok.Data;

import java.io.Serializable;

@Data
public class InventoryWarningEvent implements Serializable {
    private Long dishId;
    private String dishName;
    private Integer currentStock;

    public InventoryWarningEvent(Long dishId, String dishName, Integer currentStock) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.currentStock = currentStock;
    }
}