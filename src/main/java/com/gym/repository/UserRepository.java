package com.gym.repository;
import com.gym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface UserRepository extends JpaRepository<User, Long> {
Optional<User> findByUsername(String username);
Optional<User> findByPhone(String phone);
boolean existsByUsername(String username);
boolean existsByPhone(String phone);
List<User> findByCreatedBy(Long createdBy);
List<User> findByRole(String role);
List<User> findByRoleAndCreatedBy(String role, Long createdBy);
}
