package com.zjgsu.obl.order_service.respository;
import com.zjgsu.obl.order_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByPaymentNumber(String paymentNumber);

    Payment findByOrderId(Long orderId);

    List<Payment> findByStatus(String status);
}