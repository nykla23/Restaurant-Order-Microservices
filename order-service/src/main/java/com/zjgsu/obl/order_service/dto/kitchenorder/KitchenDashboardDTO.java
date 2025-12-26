package com.zjgsu.obl.order_service.dto.kitchenorder;

import lombok.Data;

/**
 * 后厨仪表板数据
 */
@Data
public class KitchenDashboardDTO {
    private Long waitingOrders;     // 等待制作的订单数
    private Long preparingOrders;   // 正在制作的订单数
    private Long readyOrders;       // 已出餐的订单数
    private Integer recentOrders;   // 最近一小时的订单数
    private Integer averagePrepTime; // 平均制作时间（分钟）
}
