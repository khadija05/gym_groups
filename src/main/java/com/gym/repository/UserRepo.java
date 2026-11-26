package com.gym.repo;
import com.gym.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import java.time.LocalDate;
public interface UserRepo extends JpaRepository<User,Long>{ Optional<User> findByPhone(String phone); List<User> findByGymIdAndRole(Long gymId,String role); long countByGymIdAndRole(Long gymId,String role); long countByRole(String role); }
