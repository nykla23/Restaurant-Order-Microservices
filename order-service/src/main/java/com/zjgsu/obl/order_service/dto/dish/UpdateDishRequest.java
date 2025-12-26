package com.zjgsu.obl.order_service.dto.dish;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateDishRequest {
    private String name;
    private Integer categoryId;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer stock;
    private Boolean isAvailable;
    private Integer preparationTime;
}