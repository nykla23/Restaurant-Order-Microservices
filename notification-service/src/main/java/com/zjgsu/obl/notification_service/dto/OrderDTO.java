package com.zjgsu.obl.notification_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String tableNumber;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private String status;
    private String paymentStatus;
    private String specialInstructions;
    private Integer estimatedPreparationTime;
    private Date completedAt;
    private Date cancelledAt;
    private String cancelledReason;
    private Date createdAt;
    private Date updatedAt;
    private List<OrderItemDTO> items;
//    private UserDTO user; // 用户信息（可选）
}



