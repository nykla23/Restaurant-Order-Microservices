package com.zjgsu.obl.order_service.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentDTO {
    private Long id;
    private String paymentNumber;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private Date paidAt;
    private Date createdAt;
}
