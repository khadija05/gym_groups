package com.gym.repository;
import com.gym.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface PaymentRepo extends JpaRepository<Payment,Long>{
  @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p WHERE p.gymId=:gymId") long sumByGym(@Param("gymId") Long gymId);
  @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p") long sumAll();
}
