
package com.gym.repo;
import com.gym.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import java.time.LocalDate;
public interface WorkoutRepo extends JpaRepository<WorkoutPlan,Long>{ Optional<WorkoutPlan> findTopByMemberIdOrderByIdDesc(Long memberId); }
