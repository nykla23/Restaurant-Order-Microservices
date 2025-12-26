package com.zjgsu.obl.order_service.respository;
import com.zjgsu.obl.order_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByIsActiveTrueOrderBySortOrderAsc();

    Category findByName(String name);
}