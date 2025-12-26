package com.zjgsu.obl.order_service.service;


import com.zjgsu.obl.order_service.dto.dish.CategoryDTO;
import com.zjgsu.obl.order_service.model.Category;
import com.zjgsu.obl.order_service.respository.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HttpServletRequest request;

    @Transactional
    public CategoryDTO createCategory(String name, String description, Integer sortOrder) {
        log.info("创建分类: {}", name);

        // 检查分类名是否已存在
        Category existing = categoryRepository.findByName(name);
        if (existing != null) {
            throw new RuntimeException("分类名称已存在");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setSortOrder(sortOrder != null ? sortOrder : 0);

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.fromEntity(savedCategory);
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
        return categories.stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO updateCategory(Integer categoryId, String name, String description,
                                      Integer sortOrder, Boolean isActive) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        if (name != null && !name.equals(category.getName())) {
            // 检查新名称是否已存在
            Category existing = categoryRepository.findByName(name);
            if (existing != null && !existing.getId().equals(categoryId)) {
                throw new RuntimeException("分类名称已存在");
            }
            category.setName(name);
        }

        if (description != null) {
            category.setDescription(description);
        }

        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }

        if (isActive != null) {
            category.setIsActive(isActive);
        }

        Category updatedCategory = categoryRepository.save(category);
        return CategoryDTO.fromEntity(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        // 检查分类下是否有菜品
        // 这里可以添加检查逻辑

        category.setIsActive(false);
        categoryRepository.save(category);
        log.info("禁用分类: {}", categoryId);
    }
}