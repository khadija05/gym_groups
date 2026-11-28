package com.gym.repository;
import com.gym.model.Workout; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WorkoutRepo extends JpaRepository<Workout,Long>{
  Optional<Workout> findTopByMemberIdOrderByIdDesc(Long memberId);
  List<Workout> findByMemberIdOrderByIdDesc(Long memberId);
}
