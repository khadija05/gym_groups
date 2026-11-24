package com.gym.repository;
import com.gym.model.AdminPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AdminPaymentRepository extends JpaRepository<AdminPayment, Long>{
List<AdminPayment> findByAdminId(Long adminId);
List<AdminPayment> findByStatus(String status);
}
