package com.zjgsu.obl.order_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "inventory_logs")
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(name = "change_amount", nullable = false)
    private Integer changeAmount;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(length = 20, nullable = false)
    private String type; // ORDER-订单，RESTOCK-补货，ADJUST-调整

    @Column(name = "related_id", length = 50)
    private String relatedId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }


}