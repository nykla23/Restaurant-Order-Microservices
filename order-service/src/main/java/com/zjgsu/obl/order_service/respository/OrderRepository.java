package com.zjgsu.obl.order_service.respository;
import com.zjgsu.obl.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(String status);

    List<Order> findByPaymentStatus(String paymentStatus);

    List<Order> findByUserIdAndStatus(Long userId, String status);

    // 根据状态列表查询
    List<Order> findByStatusIn(List<String> statuses);

    // 查询创建时间之后的订单
    List<Order> findByCreatedAtAfter(Date date);

    // 查询指定时间范围内的订单
    List<Order> findByCreatedAtBetween(Date startDate, Date endDate);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate")
    List<Order> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate")
    List<Order> findByStartDate(@Param("startDate") Date startDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") String status);

    @Query("SELECT SUM(o.actualAmount) FROM Order o WHERE o.status = 'COMPLETED' AND o.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();
}