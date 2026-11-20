
package com.gym.repository;
import com.gym.model.User; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UserRepository extends JpaRepository<User,Long> {
  Optional<User> findByPhone(String phone);
  Optional<User> findByUsername(String username);
  List<User> findByStatus(String status);
}
