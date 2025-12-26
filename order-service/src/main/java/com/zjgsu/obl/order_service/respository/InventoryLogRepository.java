package com.zjgsu.obl.order_service.respository;
import com.zjgsu.obl.order_service.model.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    List<InventoryLog> findByDishIdOrderByCreatedAtDesc(Long dishId);
}