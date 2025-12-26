package com.zjgsu.obl.order_service.dto.dish;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStockRequest {

    @NotNull(message = "变更数量不能为空")
    private Integer changeAmount; // 正数表示增加，负数表示减少

    private String type = "ADJUST"; // ADJUST-调整，RESTOCK-补货

    private String notes;
}