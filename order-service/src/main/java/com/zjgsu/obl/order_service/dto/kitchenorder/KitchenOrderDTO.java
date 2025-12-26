package com.zjgsu.obl.order_service.dto.kitchenorder;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class KitchenOrderDTO {
    private Long orderId;
    private String orderNumber;
    private String tableNumber;
    private String status;
    private Integer estimatedPreparationTime;
    private Date createdAt;
    private List<KitchenOrderItemDTO> items;
    private String specialInstructions;

    // 添加一些方便后厨查看的字段
    private Integer totalItems; // 总菜品数量
    private String preparationStatus; // 制作状态：WAITING, IN_PROGRESS, COMPLETED

    @Data
    public static class KitchenOrderItemDTO {
        private Long itemId;
        private Long dishId;
        private String dishName;
        private Integer quantity;
        private String specialInstructions;
        private String status; // PENDING, PREPARING, READY
        private Integer preparationTime; // 预估制作时间
    }

}

