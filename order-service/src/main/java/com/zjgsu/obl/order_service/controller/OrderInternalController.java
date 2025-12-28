package com.zjgsu.obl.order_service.controller;

import com.zjgsu.obl.order_service.common.ApiResponse;
import com.zjgsu.obl.order_service.dto.dish.DishDTO;
import com.zjgsu.obl.order_service.dto.order.OrderDTO;
import com.zjgsu.obl.order_service.service.DishService;
import com.zjgsu.obl.order_service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@Slf4j
public class OrderInternalController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DishService dishService;

    @Value("${service.auth.internal-key}")
    private String internalApiKey;

    /**
     * 内部API：获取订单详情
     */
    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderDTO> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey) {

        if (!validateInternalKey(internalKey)) {
            return ApiResponse.error(400,"内部服务认证失败");
        }

        log.debug("内部调用：获取订单，订单ID: {}", orderId);
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ApiResponse.success(orderDTO);
    }

    /**
     * 内部API：获取菜品详情
     */
    @GetMapping("/dishes/{dishId}")
    public ApiResponse<DishDTO> getDishById(
            @PathVariable Long dishId,
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey) {

        if (!validateInternalKey(internalKey)) {
            return ApiResponse.error(400,"内部服务认证失败");
        }

        log.debug("内部调用：获取菜品，菜品ID: {}", dishId);
        DishDTO dishDTO = dishService.getDishById(dishId);
        return ApiResponse.success(dishDTO);
    }

    private boolean validateInternalKey(String key) {
        return internalApiKey != null && internalApiKey.equals(key);
    }
}