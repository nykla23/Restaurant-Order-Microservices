package com.zjgsu.obl.notification_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DishDTO {
    private Long id;
    private String name;
    private CategoryDTO category;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer stock;
    private Integer totalSold;
    private Boolean isPopular;
    private Boolean isAvailable;
    private Integer preparationTime;
    private Date createdAt;
    private Date updatedAt;

}