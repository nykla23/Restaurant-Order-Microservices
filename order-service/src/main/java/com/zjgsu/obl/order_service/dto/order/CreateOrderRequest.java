package com.zjgsu.obl.order_service.dto.order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String tableNumber;
    private String specialInstructions;

    @Valid
    @NotEmpty(message = "订单项不能为空")
    private List<CreateOrderItem> items;

    @Data
    public static class CreateOrderItem {

        @NotNull(message = "菜品ID不能为空")
        private Long dishId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;

        private String specialInstructions;
    }
}


