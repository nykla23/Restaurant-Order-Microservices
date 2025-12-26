package com.zjgsu.obl.order_service.controller;
import com.zjgsu.obl.order_service.common.ApiResponse;
import com.zjgsu.obl.order_service.dto.dish.CategoryDTO;
import com.zjgsu.obl.order_service.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryDTO> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder) {
        log.info("创建分类: {}", name);
        CategoryDTO category = categoryService.createCategory(name, description, sortOrder);
        return ApiResponse.success("创建分类成功", category);
    }

    @GetMapping
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        log.info("获取所有分类");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories);
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryDTO> updateCategory(
            @PathVariable Integer categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Boolean isActive) {
        log.info("更新分类: {}", categoryId);
        CategoryDTO category = categoryService.updateCategory(categoryId, name, description, sortOrder, isActive);
        return ApiResponse.success("更新分类成功", category);
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Integer categoryId) {
        log.info("删除分类: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ApiResponse.success("删除分类成功", null);
    }
}