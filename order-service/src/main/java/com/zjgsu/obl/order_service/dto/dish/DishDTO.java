package com.zjgsu.obl.order_service.dto.dish;
import com.zjgsu.obl.order_service.model.Dish;
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

    public static DishDTO fromEntity(Dish dish) {
        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setCategory(CategoryDTO.fromEntity(dish.getCategory()));
        dto.setPrice(dish.getPrice());
        dto.setDescription(dish.getDescription());
        dto.setImageUrl(dish.getImageUrl());
        dto.setStock(dish.getStock());
        dto.setTotalSold(dish.getTotalSold());
        dto.setIsPopular(dish.getIsPopular());
        dto.setIsAvailable(dish.getIsAvailable());
        dto.setPreparationTime(dish.getPreparationTime());
        dto.setCreatedAt(dish.getCreatedAt());
        dto.setUpdatedAt(dish.getUpdatedAt());
        return dto;
    }

}