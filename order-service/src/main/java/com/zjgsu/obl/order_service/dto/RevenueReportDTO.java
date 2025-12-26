package com.zjgsu.obl.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class RevenueReportDTO {
    private Long totalOrders;              // 总订单数
    private BigDecimal totalRevenue;       // 总营收
    private BigDecimal averageOrderAmount; // 平均订单金额
    private Map<String, BigDecimal> paymentMethodStats; // 支付方式统计
}
