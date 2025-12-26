package com.zjgsu.obl.order_service.service;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单统计信息
 */
@Data
public class OrderStatistics {
    private Long totalOrders;
    private Long pendingOrders;
    private Long todayOrders;
    private BigDecimal totalRevenue;
}
