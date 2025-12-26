package com.zjgsu.obl.order_service.service;
import com.zjgsu.obl.order_service.dto.dish.DishDTO;
import com.zjgsu.obl.order_service.dto.kitchenorder.KitchenDashboardDTO;
import com.zjgsu.obl.order_service.dto.kitchenorder.KitchenOrderDTO;
import com.zjgsu.obl.order_service.dto.kitchenorder.UpdateKitchenOrderRequest;
import com.zjgsu.obl.order_service.model.Order;
import com.zjgsu.obl.order_service.model.OrderItem;
import com.zjgsu.obl.order_service.respository.OrderItemRepository;
import com.zjgsu.obl.order_service.respository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KitchenService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DishService dishService;

    /**
     * 获取后厨订单队列
     */
    public List<KitchenOrderDTO> getKitchenOrders() {
        log.info("获取后厨订单队列");

        // 获取所有需要后厨处理的订单状态
        List<String> kitchenStatuses = Arrays.asList("CONFIRMED", "PREPARING");
        List<Order> orders = orderRepository.findByStatusIn(kitchenStatuses);

        // 按照状态和创建时间排序
        orders.sort((o1, o2) -> {
            // 先按状态排序：PREPARING 在前，CONFIRMED 在后
            int statusCompare = o2.getStatus().compareTo(o1.getStatus()); // PREPARING > CONFIRMED
            if (statusCompare != 0) {
                return statusCompare;
            }
            // 相同状态按创建时间排序：早的在前
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        });

        return orders.stream()
                .map(this::convertToKitchenOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取等待准备的订单（CONFIRMED状态）
     */
    public List<KitchenOrderDTO> getWaitingOrders() {
        List<Order> orders = orderRepository.findByStatus("CONFIRMED");

        // 按创建时间排序：早的在前
        orders.sort((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));

        return orders.stream()
                .map(this::convertToKitchenOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取正在制作的订单（PREPARING状态）
     */
    public List<KitchenOrderDTO> getPreparingOrders() {
        List<Order> orders = orderRepository.findByStatus("PREPARING");

        // 按创建时间排序：早的在前
        orders.sort((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));

        return orders.stream()
                .map(this::convertToKitchenOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取待出餐的订单（READY状态）
     */
    public List<KitchenOrderDTO> getReadyOrders() {
        List<Order> orders = orderRepository.findByStatus("READY");

        // 按完成时间排序：早的在前
        orders.sort((o1, o2) -> {
            if (o1.getCompletedAt() != null && o2.getCompletedAt() != null) {
                return o1.getCompletedAt().compareTo(o2.getCompletedAt());
            }
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        });

        return orders.stream()
                .map(this::convertToKitchenOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * 开始制作订单（将订单状态从CONFIRMED改为PREPARING）
     */
    @Transactional
    public KitchenOrderDTO startPreparingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new RuntimeException("只能开始制作已确认的订单");
        }

        order.setStatus("PREPARING");
        order.setUpdatedAt(new Date());

        Order updatedOrder = orderRepository.save(order);
        log.info("开始制作订单: {}", orderId);

        return convertToKitchenOrderDTO(updatedOrder);
    }

    /**
     * 标记订单项为准备中
     */
    @Transactional
    public KitchenOrderDTO.KitchenOrderItemDTO startPreparingItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("订单项不存在"));

        Order order = orderItem.getOrder();

        // 如果订单状态还是CONFIRMED，先改为PREPARING
        if ("CONFIRMED".equals(order.getStatus())) {
            order.setStatus("PREPARING");
            orderRepository.save(order);
        }

        // 标记订单项为准备中
        // 这里我们使用特殊指令字段来存储状态，或者可以添加一个状态字段
        // 为了简单，我们假设已经有一个状态字段

        log.info("开始制作订单项: {}", orderItemId);

        return convertToKitchenOrderItemDTO(orderItem);
    }

    /**
     * 标记订单项为已完成
     */
    @Transactional
    public KitchenOrderDTO.KitchenOrderItemDTO completeItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("订单项不存在"));

        // 标记订单项为已完成
        // 这里我们使用特殊指令字段来存储状态

        log.info("完成订单项: {}", orderItemId);

        // 检查订单是否所有项都已完成
        checkAndUpdateOrderStatus(orderItem.getOrder());

        return convertToKitchenOrderItemDTO(orderItem);
    }

    /**
     * 标记订单为已出餐（READY）
     */
    @Transactional
    public KitchenOrderDTO markOrderReady(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PREPARING".equals(order.getStatus())) {
            throw new RuntimeException("只能标记正在制作的订单为已出餐");
        }

        order.setStatus("READY");
        order.setUpdatedAt(new Date());

        Order updatedOrder = orderRepository.save(order);
        log.info("标记订单为已出餐: {}", orderId);

        return convertToKitchenOrderDTO(updatedOrder);
    }

    /**
     * 批量更新订单项状态
     */
    @Transactional
    public KitchenOrderDTO updateOrderItemsStatus(Long orderId, UpdateKitchenOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        for (UpdateKitchenOrderRequest.OrderItemStatus itemStatus : request.getItemStatuses()) {
            OrderItem orderItem = orderItemRepository.findById(itemStatus.getOrderItemId())
                    .orElseThrow(() -> new RuntimeException("订单项不存在: " + itemStatus.getOrderItemId()));

            // 验证订单项属于这个订单
            if (!orderItem.getOrder().getId().equals(orderId)) {
                throw new RuntimeException("订单项不属于该订单");
            }

            // 更新订单项状态
            updateOrderItemStatus(orderItem, itemStatus.getStatus());
        }

        // 检查并更新订单状态
        checkAndUpdateOrderStatus(order);

        log.info("批量更新订单项状态，订单ID: {}", orderId);

        return convertToKitchenOrderDTO(order);
    }

    /**
     * 获取后厨仪表板数据
     */
    public KitchenDashboardDTO getDashboardData() {
        KitchenDashboardDTO dashboard = new KitchenDashboardDTO();

        // 统计各种状态的订单数量
        dashboard.setWaitingOrders(orderRepository.countByStatus("CONFIRMED"));
        dashboard.setPreparingOrders(orderRepository.countByStatus("PREPARING"));
        dashboard.setReadyOrders(orderRepository.countByStatus("READY"));

        // 获取最近一小时的订单
        Date oneHourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
        List<Order> recentOrders = orderRepository.findByCreatedAtAfter(oneHourAgo);
        dashboard.setRecentOrders(recentOrders.size());

        // 计算平均制作时间（这里简化处理）
        dashboard.setAveragePrepTime(calculateAveragePreparationTime());

        return dashboard;
    }

    /**
     * 检查并更新订单状态
     */
    private void checkAndUpdateOrderStatus(Order order) {
        // 获取订单的所有项
        List<OrderItem> items = order.getItems();

        // 检查是否所有项都已完成
        boolean allCompleted = true;
        boolean anyPreparing = false;

        for (OrderItem item : items) {
            // 这里需要根据实际的状态判断逻辑
            // 假设我们有一个方法来获取订单项状态
            String status = getOrderItemStatus(item);

            if (!"READY".equals(status)) {
                allCompleted = false;
            }

            if ("PREPARING".equals(status)) {
                anyPreparing = true;
            }
        }

        // 更新订单状态
        if (allCompleted && items.size() > 0) {
            order.setStatus("READY");
            orderRepository.save(order);
            log.info("订单所有项已完成，标记为已出餐: {}", order.getId());
        } else if (anyPreparing && !"PREPARING".equals(order.getStatus())) {
            order.setStatus("PREPARING");
            orderRepository.save(order);
            log.info("订单有项正在制作，标记为制作中: {}", order.getId());
        }
    }

    /**
     * 更新订单项状态（简化版）
     */
    private void updateOrderItemStatus(OrderItem orderItem, String status) {
        // 这里应该更新订单项的状态字段
        // 由于我们没有订单项状态字段，暂时跳过
        // orderItem.setStatus(status);
        // orderItemRepository.save(orderItem);
    }

    /**
     * 获取订单项状态（简化版）
     */
    private String getOrderItemStatus(OrderItem orderItem) {
        // 这里应该返回订单项的实际状态
        // 由于我们没有状态字段，暂时返回固定值
        return "PENDING";
    }

    /**
     * 计算平均制作时间
     */
    private Integer calculateAveragePreparationTime() {
        // 这里简化处理，返回固定值
        return 15;
    }

    /**
     * 转换Order为KitchenOrderDTO
     */
    private KitchenOrderDTO convertToKitchenOrderDTO(Order order) {
        KitchenOrderDTO dto = new KitchenOrderDTO();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTableNumber(order.getTableNumber());
        dto.setStatus(order.getStatus());
        dto.setEstimatedPreparationTime(order.getEstimatedPreparationTime());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setSpecialInstructions(order.getSpecialInstructions());

        // 转换订单项
        List<KitchenOrderDTO.KitchenOrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertToKitchenOrderItemDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        // 计算总菜品数量
        Integer totalItems = order.getItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
        dto.setTotalItems(totalItems);

        // 设置制作状态
        dto.setPreparationStatus(calculatePreparationStatus(order));

        return dto;
    }

    /**
     * 转换OrderItem为KitchenOrderItemDTO
     */
    private KitchenOrderDTO.KitchenOrderItemDTO convertToKitchenOrderItemDTO(OrderItem item) {
        KitchenOrderDTO.KitchenOrderItemDTO dto = new KitchenOrderDTO.KitchenOrderItemDTO();
        dto.setItemId(item.getId());
        dto.setDishId(item.getDishId());
        dto.setDishName(item.getDishName());
        dto.setQuantity(item.getQuantity());
        dto.setSpecialInstructions(item.getSpecialInstructions());

        // 获取菜品预估时间
        try {
            DishDTO dish = dishService.getDishById(item.getDishId());
            dto.setPreparationTime(dish.getPreparationTime());
        } catch (Exception e) {
            dto.setPreparationTime(15); // 默认15分钟
        }

        // 设置状态（这里简化处理）
        dto.setStatus("PENDING");

        return dto;
    }

    /**
     * 计算制作状态
     */
    private String calculatePreparationStatus(Order order) {
        switch (order.getStatus()) {
            case "CONFIRMED":
                return "WAITING";
            case "PREPARING":
                return "IN_PROGRESS";
            case "READY":
                return "COMPLETED";
            default:
                return "UNKNOWN";
        }
    }
}
