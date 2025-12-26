package com.zjgsu.obl.order_service.dto.statistics;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO {
    // 基础统计
    private Long totalUsers;           // 总用户数
    private Long totalOrders;          // 总订单数
    private Long totalDishes;          // 总菜品数
    private BigDecimal totalRevenue;   // 总营收

    // 今日数据
    private Long todayOrders;          // 今日订单数
    private BigDecimal todayRevenue;   // 今日营收
    private Long todayNewUsers;        // 今日新增用户

    // 订单状态统计
    private Map<String, Long> orderStatusStats;

    // 热门菜品排行
    private List<DishSalesDTO> topDishes;

    // 最近7天趋势
    private List<DailyStatsDTO> weeklyTrend;

    @Data
    public static class DishSalesDTO {
        private Long dishId;
        private String dishName;
        private Integer salesCount;        // 销量
        private BigDecimal salesAmount;    // 销售额
        private String categoryName;       // 分类名称
    }

    @Data
    public static class DailyStatsDTO {
        private String date;               // 日期
        private Long orderCount;           // 订单数
        private BigDecimal revenue;        // 营收
        private Long newUsers;             // 新增用户
    }

}

