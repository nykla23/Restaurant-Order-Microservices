package com.zjgsu.obl.order_service.service;
import com.zjgsu.obl.order_service.dto.RevenueReportDTO;
import com.zjgsu.obl.order_service.dto.statistics.StatisticsDTO;
import com.zjgsu.obl.order_service.model.Dish;
import com.zjgsu.obl.order_service.model.Order;
import com.zjgsu.obl.order_service.model.OrderItem;
import com.zjgsu.obl.order_service.respository.CategoryRepository;
import com.zjgsu.obl.order_service.respository.DishRepository;
import com.zjgsu.obl.order_service.respository.OrderItemRepository;
import com.zjgsu.obl.order_service.respository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * 获取统计概览
     */
    public StatisticsDTO getOverview() {
        log.info("获取统计概览");

        StatisticsDTO stats = new StatisticsDTO();

        // 基础统计
        stats.setTotalUsers(userRepository.count());
        stats.setTotalOrders(orderRepository.count());
        stats.setTotalDishes(dishRepository.count());

        // 总营收（只计算已支付且完成的订单）
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 今日数据
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        Date todayStart = java.sql.Timestamp.valueOf(startOfDay);
        Date todayEnd = java.sql.Timestamp.valueOf(endOfDay);

        // 今日订单
        List<Order> todayOrders = orderRepository.findByCreatedAtBetween(todayStart, todayEnd);
        stats.setTodayOrders((long) todayOrders.size());

        // 今日营收
        BigDecimal todayRevenue = todayOrders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()) && "PAID".equals(order.getPaymentStatus()))
                .map(Order::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTodayRevenue(todayRevenue);

        // 今日新增用户
        List<User> todayUsers = userRepository.findByCreatedAtBetween(todayStart, todayEnd);
        stats.setTodayNewUsers((long) todayUsers.size());

        // 订单状态统计
        stats.setOrderStatusStats(getOrderStatusStats());

        // 热门菜品排行（取前10）
        stats.setTopDishes(getTopDishes(10));

        // 最近7天趋势
        stats.setWeeklyTrend(getWeeklyTrend());

        return stats;
    }

    /**
     * 获取订单状态统计
     */
    private Map<String, Long> getOrderStatusStats() {
        List<Order> allOrders = orderRepository.findAll();

        return allOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
    }

    /**
     * 获取热门菜品排行
     */
    private List<StatisticsDTO.DishSalesDTO> getTopDishes(int limit) {
        // 获取所有已完成的订单
        List<Order> completedOrders = orderRepository.findByStatus("COMPLETED");

        // 统计菜品销量
        Map<Long, DishSalesData> dishSalesMap = new HashMap<>();

        for (Order order : completedOrders) {
            for (OrderItem item : order.getItems()) {
                DishSalesData data = dishSalesMap.getOrDefault(item.getDishId(), new DishSalesData());
                data.dishId = item.getDishId();
                data.dishName = item.getDishName();
                data.salesCount += item.getQuantity();
                data.salesAmount = data.salesAmount.add(item.getSubtotal());

                // 获取菜品分类
                try {
                    Dish dish = dishRepository.findById(item.getDishId()).orElse(null);
                    if (dish != null && dish.getCategory() != null) {
                        data.categoryName = dish.getCategory().getName();
                    }
                } catch (Exception e) {
                    // 忽略异常
                }

                dishSalesMap.put(item.getDishId(), data);
            }
        }

        // 转换为DTO并排序
        return dishSalesMap.values().stream()
                .sorted((a, b) -> b.salesCount - a.salesCount)
                .limit(limit)
                .map(data -> {
                    StatisticsDTO.DishSalesDTO dto = new StatisticsDTO.DishSalesDTO();
                    dto.setDishId(data.dishId);
                    dto.setDishName(data.dishName);
                    dto.setSalesCount(data.salesCount);
                    dto.setSalesAmount(data.salesAmount);
                    dto.setCategoryName(data.categoryName);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取最近7天趋势
     */
    private List<StatisticsDTO.DailyStatsDTO> getWeeklyTrend() {
        List<StatisticsDTO.DailyStatsDTO> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            Date startDate = java.sql.Timestamp.valueOf(startOfDay);
            Date endDate = java.sql.Timestamp.valueOf(endOfDay);

            StatisticsDTO.DailyStatsDTO daily = new StatisticsDTO.DailyStatsDTO();
            daily.setDate(date.format(formatter));

            // 当日订单
            List<Order> dailyOrders = orderRepository.findByCreatedAtBetween(startDate, endDate);
            daily.setOrderCount((long) dailyOrders.size());

            // 当日营收
            BigDecimal dailyRevenue = dailyOrders.stream()
                    .filter(order -> "COMPLETED".equals(order.getStatus()) && "PAID".equals(order.getPaymentStatus()))
                    .map(Order::getActualAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            daily.setRevenue(dailyRevenue);

            // 当日新增用户
            List<User> dailyUsers = userRepository.findByCreatedAtBetween(startDate, endDate);
            daily.setNewUsers((long) dailyUsers.size());

            trend.add(daily);
        }

        return trend;
    }

    /**
     * 获取菜品销售报表
     */
    public List<StatisticsDTO.DishSalesDTO> getDishSalesReport(Date startDate, Date endDate) {
        log.info("获取菜品销售报表，开始时间: {}，结束时间: {}", startDate, endDate);

        // 获取时间范围内的订单
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);

        Map<Long, DishSalesData> dishSalesMap = new HashMap<>();

        for (Order order : orders) {
            if ("COMPLETED".equals(order.getStatus())) {
                for (OrderItem item : order.getItems()) {
                    DishSalesData data = dishSalesMap.getOrDefault(item.getDishId(), new DishSalesData());
                    data.dishId = item.getDishId();
                    data.dishName = item.getDishName();
                    data.salesCount += item.getQuantity();
                    data.salesAmount = data.salesAmount.add(item.getSubtotal());

                    dishSalesMap.put(item.getDishId(), data);
                }
            }
        }

        return dishSalesMap.values().stream()
                .sorted((a, b) -> b.salesCount - a.salesCount)
                .map(data -> {
                    StatisticsDTO.DishSalesDTO dto = new StatisticsDTO.DishSalesDTO();
                    dto.setDishId(data.dishId);
                    dto.setDishName(data.dishName);
                    dto.setSalesCount(data.salesCount);
                    dto.setSalesAmount(data.salesAmount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取收入报表
     */
    public RevenueReportDTO getRevenueReport(Date startDate, Date endDate) {
        log.info("获取收入报表，开始时间: {}，结束时间: {}", startDate, endDate);

        RevenueReportDTO report = new RevenueReportDTO();

        // 获取时间范围内的订单
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);

        // 总订单数
        report.setTotalOrders((long) orders.size());

        // 总营收
        BigDecimal totalRevenue = orders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()) && "PAID".equals(order.getPaymentStatus()))
                .map(Order::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.setTotalRevenue(totalRevenue);

        // 平均订单金额
        long completedPaidOrders = orders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()) && "PAID".equals(order.getPaymentStatus()))
                .count();

        if (completedPaidOrders > 0) {
            report.setAverageOrderAmount(totalRevenue.divide(
                    BigDecimal.valueOf(completedPaidOrders), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            report.setAverageOrderAmount(BigDecimal.ZERO);
        }

        // 支付方式统计
        Map<String, BigDecimal> paymentMethodStats = new HashMap<>();
        orders.stream()
                .filter(order -> "PAID".equals(order.getPaymentStatus()))
                .forEach(order -> {
                    // 获取支付记录
                    // 这里简化处理，实际应该从支付表中查询
                    String paymentMethod = "UNKNOWN";
                    paymentMethodStats.put(paymentMethod,
                            paymentMethodStats.getOrDefault(paymentMethod, BigDecimal.ZERO)
                                    .add(order.getActualAmount()));
                });
        report.setPaymentMethodStats(paymentMethodStats);

        return report;
    }

    // 内部类：菜品销售数据
    private static class DishSalesData {
        Long dishId;
        String dishName;
        String categoryName;
        int salesCount = 0;
        BigDecimal salesAmount = BigDecimal.ZERO;
    }
}
