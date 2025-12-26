package com.zjgsu.obl.order_service.dto.dish;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDishRequest {

    @NotBlank(message = "菜品名称不能为空")
    private String name;

    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private String description;
    private String imageUrl;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    private Integer preparationTime = 15;
}