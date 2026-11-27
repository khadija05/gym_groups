package com.gym.repository;
import com.gym.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface AttendanceRepo extends JpaRepository<Attendance,Long>{
  long countByGymIdAndDate(Long gymId, LocalDate date);
  long countByDate(LocalDate date);
  List<Attendance> findByDateOrderByIdDesc(LocalDate date);
}
