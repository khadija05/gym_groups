package com.gym.repository;
import com.gym.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MemberRepo extends JpaRepository<Member,Long>{
  Optional<Member> findByUserId(Long userId);
  Optional<Member> findByUserIdAndGymId(Long userId,Long gymId);
}
