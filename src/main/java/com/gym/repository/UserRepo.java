package com.gym.repository;
import com.gym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepo extends JpaRepository<User,Long>{
  Optional<User> findByPhone(String phone);
  List<User> findByGymIdAndRole(Long gymId,String role);
  List<User> findByRole(String role);
  long countByGymIdAndRole(Long gymId,String role);
  long countByRole(String role);
}
