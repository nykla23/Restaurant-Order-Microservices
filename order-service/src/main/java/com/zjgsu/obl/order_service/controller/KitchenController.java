package com.zjgsu.obl.order_service.controller;
import com.zjgsu.obl.order_service.common.ApiResponse;
import com.zjgsu.obl.order_service.dto.kitchenorder.KitchenDashboardDTO;
import com.zjgsu.obl.order_service.dto.kitchenorder.KitchenOrderDTO;
import com.zjgsu.obl.order_service.dto.kitchenorder.UpdateKitchenOrderRequest;
import com.zjgsu.obl.order_service.service.KitchenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
@Validated
public class KitchenController {

    private static final Logger log = LoggerFactory.getLogger(KitchenController.class);

    @Autowired
    private KitchenService kitchenService;

    /**
     * 获取后厨订单队列
     */
    @GetMapping("/orders")
    public ApiResponse<List<KitchenOrderDTO>> getKitchenOrders() {
        log.info("获取后厨订单队列");
        List<KitchenOrderDTO> orders = kitchenService.getKitchenOrders();
        return ApiResponse.success(orders);
    }

    /**
     * 获取等待制作的订单
     */
    @GetMapping("/orders/waiting")
    public ApiResponse<List<KitchenOrderDTO>> getWaitingOrders() {
        log.info("获取等待制作的订单");
        List<KitchenOrderDTO> orders = kitchenService.getWaitingOrders();
        return ApiResponse.success(orders);
    }

    /**
     * 获取正在制作的订单
     */
    @GetMapping("/orders/preparing")
    public ApiResponse<List<KitchenOrderDTO>> getPreparingOrders() {
        log.info("获取正在制作的订单");
        List<KitchenOrderDTO> orders = kitchenService.getPreparingOrders();
        return ApiResponse.success(orders);
    }

    /**
     * 获取已出餐的订单
     */
    @GetMapping("/orders/ready")
    public ApiResponse<List<KitchenOrderDTO>> getReadyOrders() {
        log.info("获取已出餐的订单");
        List<KitchenOrderDTO> orders = kitchenService.getReadyOrders();
        return ApiResponse.success(orders);
    }

    /**
     * 开始制作订单
     */
    @PostMapping("/orders/{orderId}/start")
    public ApiResponse<KitchenOrderDTO> startPreparingOrder(@PathVariable Long orderId) {
        log.info("开始制作订单: {}", orderId);
        KitchenOrderDTO order = kitchenService.startPreparingOrder(orderId);
        return ApiResponse.success("开始制作订单", order);
    }

    /**
     * 标记订单为已出餐
     */
    @PostMapping("/orders/{orderId}/ready")
    public ApiResponse<KitchenOrderDTO> markOrderReady(@PathVariable Long orderId) {
        log.info("标记订单为已出餐: {}", orderId);
        KitchenOrderDTO order = kitchenService.markOrderReady(orderId);
        return ApiResponse.success("订单已标记为已出餐", order);
    }

    /**
     * 批量更新订单项状态
     */
    @PutMapping("/orders/{orderId}/items")
    public ApiResponse<KitchenOrderDTO> updateOrderItemsStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateKitchenOrderRequest request) {
        log.info("批量更新订单项状态，订单ID: {}", orderId);
        KitchenOrderDTO order = kitchenService.updateOrderItemsStatus(orderId, request);
        return ApiResponse.success("订单项状态更新成功", order);
    }

    /**
     * 标记订单项为准备中
     */
    @PostMapping("/items/{itemId}/start")
    public ApiResponse<KitchenOrderDTO.KitchenOrderItemDTO> startPreparingItem(@PathVariable Long itemId) {
        log.info("开始制作订单项: {}", itemId);
        KitchenOrderDTO.KitchenOrderItemDTO item = kitchenService.startPreparingItem(itemId);
        return ApiResponse.success("开始制作订单项", item);
    }

    /**
     * 标记订单项为已完成
     */
    @PostMapping("/items/{itemId}/complete")
    public ApiResponse<KitchenOrderDTO.KitchenOrderItemDTO> completeItem(@PathVariable Long itemId) {
        log.info("完成订单项: {}", itemId);
        KitchenOrderDTO.KitchenOrderItemDTO item = kitchenService.completeItem(itemId);
        return ApiResponse.success("订单项已完成", item);
    }

    /**
     * 获取后厨仪表板数据
     */
    @GetMapping("/dashboard")
    public ApiResponse<KitchenDashboardDTO> getDashboardData() {
        log.info("获取后厨仪表板数据");
        KitchenDashboardDTO dashboard = kitchenService.getDashboardData();
        return ApiResponse.success(dashboard);
    }
}