package com.zjgsu.obl.order_service.controller;
import com.zjgsu.obl.order_service.common.ApiResponse;
import com.zjgsu.obl.order_service.dto.order.*;
import com.zjgsu.obl.order_service.service.OrderService;
import com.zjgsu.obl.order_service.service.OrderStatistics;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("创建订单，用户ID: {}", request.getUserId());
        OrderDTO order = orderService.createOrder(request);
        return ApiResponse.success("订单创建成功", order);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDTO> getOrder(@PathVariable Long orderId) {
        log.info("获取订单详情: {}", orderId);
        OrderDTO order = orderService.getOrderById(orderId);
        return ApiResponse.success(order);
    }

    /**
     * 根据订单号获取订单
     */
    @GetMapping("/number/{orderNumber}")
    public ApiResponse<OrderDTO> getOrderByNumber(@PathVariable String orderNumber) {
        log.info("根据订单号获取订单: {}", orderNumber);
        OrderDTO order = orderService.getOrderByNumber(orderNumber);
        return ApiResponse.success(order);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        log.info("获取用户订单列表: {}", userId);
        List<OrderDTO> orders = orderService.getUserOrders(userId);
        return ApiResponse.success(orders);
    }

    /**
     * 获取指定状态的订单列表
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<OrderDTO>> getOrdersByStatus(@PathVariable String status) {
        log.info("获取状态为 {} 的订单列表", status);
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ApiResponse.success(orders);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("更新订单状态: {} -> {}", orderId, request.getStatus());
        OrderDTO order = orderService.updateOrderStatus(orderId, request);
        return ApiResponse.success("订单状态更新成功", order);
    }

    /**
     * 处理订单支付
     */
    @PostMapping("/{orderId}/payment")
    public ApiResponse<PaymentDTO> processPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody ProcessPaymentRequest request) {
        log.info("处理订单支付: {}, 支付方式: {}", orderId, request.getPaymentMethod());
        PaymentDTO payment = orderService.processPayment(orderId, request);

        if ("SUCCESS".equals(payment.getStatus())) {
            return ApiResponse.success("支付成功", payment);
        } else {
            return ApiResponse.success("支付失败", payment);
        }
    }

    /**
     * 获取订单支付记录
     */
    @GetMapping("/{orderId}/payment")
    public ApiResponse<PaymentDTO> getOrderPayment(@PathVariable Long orderId) {
        log.info("获取订单支付记录: {}", orderId);
        PaymentDTO payment = orderService.getOrderPayment(orderId);
        return ApiResponse.success(payment);
    }

    /**
     * 获取订单统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<OrderStatistics> getStatistics() {
        log.info("获取订单统计信息");
        OrderStatistics stats = orderService.getStatistics();
        return ApiResponse.success(stats);
    }
}