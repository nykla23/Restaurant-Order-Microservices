package com.zjgsu.obl.notification_service.respository;

import com.zjgsu.obl.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);

    List<Notification> findByUserIdIsNullOrderByCreatedAtDesc(); // 广播通知

    List<Notification> findByUserIdAndPriorityOrderByCreatedAtDesc(Long userId, String priority);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND (n.expiresAt IS NULL OR n.expiresAt > :now) ORDER BY n.createdAt DESC")
    List<Notification> findValidByUserId(@Param("userId") Long userId, @Param("now") Date now);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.status = 'UNREAD'")
    Long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.priority = 'URGENT' AND n.status = 'UNREAD'")
    Long countUrgentUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.type = :type AND n.status = 'UNREAD'")
    Long countUnreadByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = :readAt WHERE n.userId = :userId AND n.status = 'UNREAD'")
    int markAllAsRead(@Param("userId") Long userId, @Param("readAt") Date readAt);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = :readAt WHERE n.id = :id AND n.userId = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, @Param("readAt") Date readAt);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt < :now")
    int deleteExpired(@Param("now") Date now);
}