package com.gym.repository;
import com.gym.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
    // MPYA - Tafuta bila kujali herufi kubwa/ndogo
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByPhoneIgnoreCase(String phone);
    java.util.List<User> findByCreatedBy(Long createdBy);
    java.util.List<User> findByRoleAndCreatedBy(String role, Long createdBy);
}
