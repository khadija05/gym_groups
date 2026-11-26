
package com.gym.repo;
import com.gym.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import java.time.LocalDate;
public interface MemberRepo extends JpaRepository<Member,Long>{ Optional<Member> findByUserId(Long userId); Optional<Member> findByUserIdAndGymId(Long userId,Long gymId); }
