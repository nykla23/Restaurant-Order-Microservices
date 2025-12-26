package com.zjgsu.obl.order_service.model;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_number", unique = true, nullable = false, length = 50)
    private String paymentNumber;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 20)
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED, REFUNDED

    @Column(name = "payment_method", length = 20)
    private String paymentMethod; // CASH, WECHAT, ALIPAY, CARD

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "payer_id", length = 50)
    private String payerId;

    @Column(name = "payer_info", columnDefinition = "TEXT")
    private String payerInfo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paid_at")
    private Date paidAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "refunded_at")
    private Date refundedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();

        // 生成支付流水号
        if (paymentNumber == null) {
            paymentNumber = "PAY" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}