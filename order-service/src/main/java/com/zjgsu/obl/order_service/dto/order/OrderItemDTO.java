package com.zjgsu.obl.order_service.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItemDTO {
    private Long id;
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String specialInstructions;
    private Date createdAt;
}