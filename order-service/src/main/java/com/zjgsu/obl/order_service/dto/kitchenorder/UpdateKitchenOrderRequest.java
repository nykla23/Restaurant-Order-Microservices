package com.zjgsu.obl.order_service.dto.kitchenorder;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateKitchenOrderRequest {

    @NotEmpty(message = "订单项状态列表不能为空")
    private List<OrderItemStatus> itemStatuses;

    @Data
    public static class OrderItemStatus {
        @NotEmpty(message = "订单项ID不能为空")
        private Long orderItemId;

        @NotEmpty(message = "状态不能为空")
        private String status; // PENDING, PREPARING, READY
    }

}

