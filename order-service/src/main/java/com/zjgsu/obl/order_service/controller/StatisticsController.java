package com.zjgsu.obl.order_service.controller;
import com.zjgsu.obl.order_service.common.ApiResponse;
import com.zjgsu.obl.order_service.dto.RevenueReportDTO;
import com.zjgsu.obl.order_service.dto.statistics.StatisticsDTO;
import com.zjgsu.obl.order_service.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Slf4j
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取统计概览
     */
    @GetMapping("/overview")
    public ApiResponse<StatisticsDTO> getOverview() {
        log.info("获取统计概览");
        StatisticsDTO stats = statisticsService.getOverview();
        return ApiResponse.success(stats);
    }

    /**
     * 获取菜品销售报表
     */
    @GetMapping("/dish-sales")
    public ApiResponse<List<StatisticsDTO.DishSalesDTO>> getDishSalesReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        log.info("获取菜品销售报表，开始时间: {}，结束时间: {}", startDate, endDate);
        List<StatisticsDTO.DishSalesDTO> report = statisticsService.getDishSalesReport(startDate, endDate);
        return ApiResponse.success(report);
    }

    /**
     * 获取收入报表
     */
    @GetMapping("/revenue")
    public ApiResponse<RevenueReportDTO> getRevenueReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        log.info("获取收入报表，开始时间: {}，结束时间: {}", startDate, endDate);
        RevenueReportDTO report = statisticsService.getRevenueReport(startDate, endDate);
        return ApiResponse.success(report);
    }
}