package com.zjgsu.obl.order_service.service;
import com.zjgsu.obl.order_service.dto.dish.CreateDishRequest;
import com.zjgsu.obl.order_service.dto.dish.DishDTO;
import com.zjgsu.obl.order_service.dto.dish.UpdateDishRequest;
import com.zjgsu.obl.order_service.dto.dish.UpdateStockRequest;
import com.zjgsu.obl.order_service.event.EventPublisher;
import com.zjgsu.obl.order_service.model.Category;
import com.zjgsu.obl.order_service.model.Dish;
import com.zjgsu.obl.order_service.model.InventoryLog;
import com.zjgsu.obl.order_service.respository.CategoryRepository;
import com.zjgsu.obl.order_service.respository.DishRepository;
import com.zjgsu.obl.order_service.respository.InventoryLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishService {


    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    @Autowired
    private EventPublisher eventPublisher;


    @Transactional
    public DishDTO createDish(CreateDishRequest request) {
        log.info("创建菜品: {}", request.getName());

        // 验证分类是否存在
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        Dish dish = new Dish();
        dish.setName(request.getName());
        dish.setCategory(category);
        dish.setPrice(request.getPrice());
        dish.setDescription(request.getDescription());
        dish.setImageUrl(request.getImageUrl());
        dish.setStock(request.getStock());
        dish.setPreparationTime(request.getPreparationTime());
        dish.setIsAvailable(true);

        Dish savedDish = dishRepository.save(dish);

        // 记录库存变更日志
        if (request.getStock() > 0) {
            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setDish(savedDish);
            inventoryLog.setChangeAmount(request.getStock());
            inventoryLog.setCurrentStock(request.getStock());
            inventoryLog.setType("RESTOCK");
            inventoryLog.setNotes("初始库存");
            inventoryLogRepository.save(inventoryLog);
        }

        return DishDTO.fromEntity(savedDish);
    }

    public DishDTO getDishById(Long dishId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));
        return DishDTO.fromEntity(dish);
    }

    public List<DishDTO> getAllDishes() {
        List<Dish> dishes = dishRepository.findByIsAvailableTrue();
        return dishes.stream()
                .map(DishDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DishDTO> getDishesByCategory(Integer categoryId) {
        List<Dish> dishes = dishRepository.findByCategoryId(categoryId);
        return dishes.stream()
                .filter(Dish::getIsAvailable)
                .map(DishDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DishDTO> getPopularDishes() {
        List<Dish> dishes = dishRepository.findByIsPopularTrueAndIsAvailableTrue();
        return dishes.stream()
                .map(DishDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DishDTO> searchDishes(String keyword) {
        List<Dish> dishes = dishRepository.search(keyword);
        return dishes.stream()
                .map(DishDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DishDTO updateDish(Long dishId, UpdateDishRequest request) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));

        if (request.getName() != null) {
            dish.setName(request.getName());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            dish.setCategory(category);
        }

        if (request.getPrice() != null) {
            dish.setPrice(request.getPrice());
        }

        if (request.getDescription() != null) {
            dish.setDescription(request.getDescription());
        }

        if (request.getImageUrl() != null) {
            dish.setImageUrl(request.getImageUrl());
        }

        if (request.getStock() != null) {
            int oldStock = dish.getStock();
            int newStock = request.getStock();
            dish.setStock(newStock);

            // 记录库存变更
            if (newStock != oldStock) {
                InventoryLog inventoryLog = new InventoryLog();
                inventoryLog.setDish(dish);
                inventoryLog.setChangeAmount(newStock - oldStock);
                inventoryLog.setCurrentStock(newStock);
                inventoryLog.setType("ADJUST");
                inventoryLog.setNotes("手动调整库存");
                inventoryLogRepository.save(inventoryLog);
            }
        }

        if (request.getIsAvailable() != null) {
            dish.setIsAvailable(request.getIsAvailable());
        }

        if (request.getPreparationTime() != null) {
            dish.setPreparationTime(request.getPreparationTime());
        }

        Dish updatedDish = dishRepository.save(dish);
        return DishDTO.fromEntity(updatedDish);
    }

    @Transactional
    public void updatePopularStatus(Long dishId, Boolean isPopular) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));

        dish.setIsPopular(isPopular);
        dishRepository.save(dish);
        log.info("更新菜品热门状态: {} -> {}", dishId, isPopular);
    }

    @Transactional
    public DishDTO updateStock(Long dishId, UpdateStockRequest request) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("菜品不存在"));

        int oldStock = dish.getStock();
        int newStock = oldStock + request.getChangeAmount();

        if (newStock < 0) {
            throw new RuntimeException("库存不能为负数");
        }

        dish.setStock(newStock);
        Dish updatedDish = dishRepository.save(dish);

        // 记录库存变更日志
        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setDish(dish);
        inventoryLog.setChangeAmount(request.getChangeAmount());
        inventoryLog.setCurrentStock(newStock);
        inventoryLog.setType(request.getType());
        inventoryLog.setNotes(request.getNotes());
        inventoryLogRepository.save(inventoryLog);

        log.info("更新菜品库存: {} 变更: {} 新库存: {}", dishId, request.getChangeAmount(), newStock);

        return DishDTO.fromEntity(updatedDish);
    }

    /**
     * 扣减库存（用于订单）
     */
    public boolean reduceStock(Long dishId, Integer quantity) {
        try {
            Dish dish = dishRepository.findById(dishId)
                    .orElseThrow(() -> new RuntimeException("菜品不存在"));

            if (dish.getStock() < quantity) {
                log.warn("库存不足: {} 库存: {} 需求: {}", dishId, dish.getStock(), quantity);
                return false;
            }

            int newStock = dish.getStock() - quantity;
            dish.setStock(newStock);
            // 修复：处理 totalSold 为 null 的情况
            Integer totalSold = dish.getTotalSold();
            if (totalSold == null) {
                totalSold = 0;
                log.info("菜品ID: {} 的 totalSold 为 null，已重置为 0", dishId);
            }
            dish.setTotalSold(totalSold + quantity);

            dishRepository.save(dish);

            // 记录库存变更日志
            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setDish(dish);
            inventoryLog.setChangeAmount(-quantity);
            inventoryLog.setCurrentStock(newStock);
            inventoryLog.setType("ORDER");
            inventoryLog.setNotes("订单扣减库存");
            inventoryLogRepository.save(inventoryLog);

            log.info("扣减库存成功: {} 数量: {} 新库存: {}", dishId, quantity, newStock);

            // 检查库存预警 - 发布事件而不是直接调用
            checkLowStockWarning(dish);

            return true;
        } catch (Exception e) {
            log.error("扣减库存失败: {}", dishId, e);
            return false;
        }
    }

    /**
     * 增加库存（用于取消订单等）
     */
    @Transactional
    public boolean increaseStock(Long dishId, Integer quantity) {
        try {
            Dish dish = dishRepository.findById(dishId)
                    .orElseThrow(() -> new RuntimeException("菜品不存在"));

            int newStock = dish.getStock() + quantity;
            dish.setStock(newStock);
            dishRepository.save(dish);

            // 记录库存变更日志
            InventoryLog inventoryLog = new InventoryLog();
            inventoryLog.setDish(dish);
            inventoryLog.setChangeAmount(quantity);
            inventoryLog.setCurrentStock(newStock);
            inventoryLog.setType("ADJUST");
            inventoryLog.setNotes("库存回滚");
            inventoryLogRepository.save(inventoryLog);

            log.info("增加库存成功: {} 数量: {} 新库存: {}", dishId, quantity, newStock);
            return true;
        } catch (Exception e) {
            log.error("增加库存失败: {}", dishId, e);
            return false;
        }
    }


    // 在 reduceStock 方法中添加库存预警
    private void checkLowStockWarning(Dish dish) {
        int lowStockThreshold = 10;

        if (dish.getStock() <= lowStockThreshold) {
            log.warn("菜品库存预警: {} 当前库存: {}", dish.getName(), dish.getStock());

            // 发布库存预警事件，而不是直接调用notificationService
            eventPublisher.publishInventoryWarning(dish.getId(), dish.getName(), dish.getStock());
        }
    }
}