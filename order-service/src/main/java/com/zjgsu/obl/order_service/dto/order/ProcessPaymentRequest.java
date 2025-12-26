package com.zjgsu.obl.order_service.dto.order;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessPaymentRequest {

    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod; // CASH, WECHAT, ALIPAY, CARD

    private BigDecimal amount; // 支付金额，如果不传则使用订单实际金额

    private String payerInfo; // 付款人信息（可选）
}