package com.zjgsu.obl.order_service.dto.dish;
import com.zjgsu.obl.order_service.model.Category;
import lombok.Data;

import java.util.Date;

@Data
public class CategoryDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
    private Date createdAt;

    public static CategoryDTO fromEntity(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        dto.setIsActive(category.getIsActive());
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }
}