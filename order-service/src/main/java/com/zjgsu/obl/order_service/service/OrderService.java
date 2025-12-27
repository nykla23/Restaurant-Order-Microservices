package com.zjgsu.obl.order_service.service;
import com.zjgsu.obl.order_service.client.UserClient;
import com.zjgsu.obl.order_service.dto.dish.DishDTO;
import com.zjgsu.obl.order_service.dto.order.*;
import com.zjgsu.obl.order_service.event.EventPublisher;
import com.zjgsu.obl.order_service.model.Order;
import com.zjgsu.obl.order_service.model.OrderItem;
import com.zjgsu.obl.order_service.model.Payment;
import com.zjgsu.obl.order_service.respository.OrderItemRepository;
import com.zjgsu.obl.order_service.respository.OrderRepository;
import com.zjgsu.obl.order_service.respository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DishService dishService;

//    @Autowired
//    private UserRepository userRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private UserClient userClient;

    /**
     * 创建订单
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("创建订单，用户ID: {}", request.getUserId());

        // 验证用户是否存在
        userClient.getUserById(request.getUserId());

        // 创建订单实体
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTableNumber(request.getTableNumber());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");

        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (CreateOrderRequest.CreateOrderItem itemRequest : request.getItems()) {
            // 获取菜品信息
            DishDTO dishDTO = dishService.getDishById(itemRequest.getDishId());

            // 扣减库存
            boolean stockReduced = dishService.reduceStock(itemRequest.getDishId(), itemRequest.getQuantity());
            if (!stockReduced) {
                throw new RuntimeException("菜品 '" + dishDTO.getName() + "' 库存不足");
            }

            // 创建订单项
            OrderItem item = new OrderItem();
            item.setDishId(dishDTO.getId());
            item.setDishName(dishDTO.getName());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(dishDTO.getPrice());
            item.setSubtotal(dishDTO.getPrice().multiply(new BigDecimal(itemRequest.getQuantity())));
            item.setSpecialInstructions(itemRequest.getSpecialInstructions());
            item.setOrder(order);

            items.add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // 设置订单金额
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount.subtract(order.getDiscountAmount()));
        order.setItems(items);

        // 计算预估制作时间（取最长的菜品准备时间）
        int maxPrepTime = items.stream()
                .mapToInt(item -> {
                    DishDTO dish = dishService.getDishById(item.getDishId());
                    return dish.getPreparationTime() != null ? dish.getPreparationTime() : 15;
                })
                .max()
                .orElse(15);
        order.setEstimatedPreparationTime(maxPrepTime);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 保存订单项
        for (OrderItem item : items) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        log.info("订单创建成功，订单号: {}, 总金额: {}", savedOrder.getOrderNumber(), savedOrder.getTotalAmount());

        // 发布订单创建事件，而不是直接调用notificationService
        eventPublisher.publishOrderCreated(
                savedOrder.getId(), savedOrder.getOrderNumber(), savedOrder.getUserId());

        return convertToDTO(savedOrder);
    }

    /**
     * 获取订单详情
     */
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return convertToDTO(order);
    }

    /**
     * 根据订单号获取订单
     */
    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToDTO(order);
    }

    /**
     * 获取用户订单列表
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定状态的订单列表
     */
    public List<OrderDTO> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        String oldStatus = order.getStatus();
        String newStatus = request.getStatus();

        // 验证状态转换是否有效
        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new RuntimeException("无效的状态转换: " + oldStatus + " -> " + newStatus);
        }

        // 更新状态
        order.setStatus(newStatus);

        // 如果是取消订单，需要回滚库存
        if ("CANCELLED".equals(newStatus)) {
            order.setCancelledAt(new Date());
            order.setCancelledReason(request.getCancelledReason());

            // 回滚库存
            rollbackStock(order);
        }

        // 如果是已完成，记录完成时间
        if ("COMPLETED".equals(newStatus)) {
            order.setCompletedAt(new Date());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("订单状态更新: {} -> {}, 订单ID: {}", oldStatus, newStatus, orderId);

        // 发布订单状态变更事件，而不是直接调用notificationService
        eventPublisher.publishOrderStatusChanged(
                orderId, order.getUserId(), oldStatus, newStatus);

        return convertToDTO(updatedOrder);
    }

    /**
     * 处理支付
     */
    @Transactional
    public PaymentDTO processPayment(Long orderId, ProcessPaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 检查订单是否已支付
        if ("PAID".equals(order.getPaymentStatus())) {
            throw new RuntimeException("订单已支付");
        }

        // 检查订单状态是否可以支付
        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("订单已取消，无法支付");
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(request.getAmount() != null ? request.getAmount() : order.getActualAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPayerInfo(request.getPayerInfo());

        // 模拟支付处理（这里模拟90%成功率）
        boolean paymentSuccess = Math.random() < 0.9;

        if (paymentSuccess) {
            payment.setStatus("SUCCESS");
            payment.setPaidAt(new Date());
            payment.setTransactionId("TXN" + System.currentTimeMillis());

            // 更新订单支付状态
            order.setPaymentStatus("PAID");
            orderRepository.save(order);

            log.info("支付成功，订单ID: {}, 支付金额: {}", orderId, payment.getAmount());
        } else {
            payment.setStatus("FAILED");
            log.warn("支付失败，订单ID: {}", orderId);
        }

        Payment savedPayment = paymentRepository.save(payment);

        return convertToPaymentDTO(savedPayment);
    }

    /**
     * 获取订单支付记录
     */
    public PaymentDTO getOrderPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        return convertToPaymentDTO(payment);
    }

    /**
     * 验证状态转换是否有效
     */
    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        // 简单的状态机验证
        switch (oldStatus) {
            case "PENDING":
                return "CONFIRMED".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "CONFIRMED":
                return "PREPARING".equals(newStatus) || "CANCELLED".equals(newStatus);
            case "PREPARING":
                return "READY".equals(newStatus);
            case "READY":
                return "COMPLETED".equals(newStatus);
            case "COMPLETED":
            case "CANCELLED":
                return false; // 最终状态不能转换
            default:
                return false;
        }
    }

    /**
     * 回滚库存（订单取消时）
     */
    private void rollbackStock(Order order) {
        for (OrderItem item : order.getItems()) {
            try {
                dishService.increaseStock(item.getDishId(), item.getQuantity());
                log.info("库存回滚成功，菜品ID: {}, 数量: {}", item.getDishId(), item.getQuantity());
            } catch (Exception e) {
                log.error("库存回滚失败，菜品ID: {}", item.getDishId(), e);
            }
        }
    }

    /**
     * 转换Order实体为DTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);

        // 转换订单项
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    /**
     * 转换OrderItem实体为DTO
     */
    private OrderItemDTO convertToItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }

    /**
     * 转换Payment实体为DTO
     */
    private PaymentDTO convertToPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(payment, dto);
        return dto;
    }

    /**
     * 获取统计数据
     */
    public OrderStatistics getStatistics() {
        OrderStatistics stats = new OrderStatistics();

        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByStatus("PENDING"));
        stats.setTodayOrders(orderRepository.countByStatus("PENDING")); // 简化处理
        stats.setTotalRevenue(orderRepository.getTotalRevenue() != null ?
                orderRepository.getTotalRevenue() : BigDecimal.ZERO);

        return stats;
    }
}

