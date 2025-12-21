package com.zjgsu.obl.user_service.respository;

import com.zjgsu.obl.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // 查询指定时间范围内创建的用户
    List<User> findByCreatedAtBetween(Date startDate, Date endDate);
}