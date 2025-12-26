package com.zjgsu.obl.order_service.controller;
import com.zjgsu.obl.order_service.common.ApiResponse;
import com.zjgsu.obl.order_service.dto.dish.CreateDishRequest;
import com.zjgsu.obl.order_service.dto.dish.DishDTO;
import com.zjgsu.obl.order_service.dto.dish.UpdateDishRequest;
import com.zjgsu.obl.order_service.dto.dish.UpdateStockRequest;
import com.zjgsu.obl.order_service.service.DishService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@Slf4j
@Validated
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public ApiResponse<DishDTO> createDish(@Valid @RequestBody CreateDishRequest request) {
        log.info("创建菜品: {}", request.getName());
        DishDTO dish = dishService.createDish(request);
        return ApiResponse.success("创建菜品成功", dish);
    }

    @GetMapping("/{dishId}")
    public ApiResponse<DishDTO> getDish(@PathVariable Long dishId) {
        log.info("获取菜品: {}", dishId);
        DishDTO dish = dishService.getDishById(dishId);
        return ApiResponse.success(dish);
    }

    @GetMapping
    public ApiResponse<List<DishDTO>> getAllDishes() {
        log.info("获取所有菜品");
        List<DishDTO> dishes = dishService.getAllDishes();
        return ApiResponse.success(dishes);
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<DishDTO>> getDishesByCategory(@PathVariable Integer categoryId) {
        log.info("获取分类菜品: {}", categoryId);
        List<DishDTO> dishes = dishService.getDishesByCategory(categoryId);
        return ApiResponse.success(dishes);
    }

    @GetMapping("/popular")
    public ApiResponse<List<DishDTO>> getPopularDishes() {
        log.info("获取热门菜品");
        List<DishDTO> dishes = dishService.getPopularDishes();
        return ApiResponse.success(dishes);
    }

    @GetMapping("/search")
    public ApiResponse<List<DishDTO>> searchDishes(@RequestParam String keyword) {
        log.info("搜索菜品: {}", keyword);
        List<DishDTO> dishes = dishService.searchDishes(keyword);
        return ApiResponse.success(dishes);
    }

    @PutMapping("/{dishId}")
    public ApiResponse<DishDTO> updateDish(
            @PathVariable Long dishId,
            @Valid @RequestBody UpdateDishRequest request) {
        log.info("更新菜品: {}", dishId);
        DishDTO dish = dishService.updateDish(dishId, request);
        return ApiResponse.success("更新菜品成功", dish);
    }

    @PutMapping("/{dishId}/popular")
    public ApiResponse<Void> updatePopularStatus(
            @PathVariable Long dishId,
            @RequestParam Boolean isPopular) {
        log.info("更新菜品热门状态: {} -> {}", dishId, isPopular);
        dishService.updatePopularStatus(dishId, isPopular);
        return ApiResponse.success("更新热门状态成功", null);
    }

    @PutMapping("/{dishId}/stock")
    public ApiResponse<DishDTO> updateStock(
            @PathVariable Long dishId,
            @Valid @RequestBody UpdateStockRequest request) {
        log.info("更新菜品库存: {}", dishId);
        DishDTO dish = dishService.updateStock(dishId, request);
        return ApiResponse.success("更新库存成功", dish);
    }
}