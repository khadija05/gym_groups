package com.gym.repository;
import com.gym.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface WorkoutRepo extends JpaRepository<WorkoutPlan,Long>{
  Optional<WorkoutPlan> findTopByMemberIdOrderByIdDesc(Long memberId);
}
