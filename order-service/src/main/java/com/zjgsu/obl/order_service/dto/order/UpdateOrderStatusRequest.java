package com.zjgsu.obl.order_service.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotBlank(message = "状态不能为空")
    private String status; // PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED

    private String cancelledReason; // 如果是取消订单，可以填写原因
}