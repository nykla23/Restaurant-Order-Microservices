package com.zjgsu.obl.order_service.respository;
import com.zjgsu.obl.order_service.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    List<Dish> findByIsAvailableTrue();

    List<Dish> findByCategoryId(Integer categoryId);

    List<Dish> findByIsPopularTrueAndIsAvailableTrue();

    @Query("SELECT d FROM Dish d WHERE d.isAvailable = true AND (d.name LIKE %:keyword% OR d.description LIKE %:keyword%)")
    List<Dish> search(@Param("keyword") String keyword);

    @Modifying
    @Query("UPDATE Dish d SET d.stock = d.stock + :quantity WHERE d.id = :dishId")
    void updateStock(@Param("dishId") Long dishId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Dish d SET d.totalSold = d.totalSold + :quantity WHERE d.id = :dishId")
    void updateTotalSold(@Param("dishId") Long dishId, @Param("quantity") Integer quantity);
}