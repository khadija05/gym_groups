package com.gym.repo;
import com.gym.model.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import java.time.LocalDate;
public interface AttendanceRepo extends JpaRepository<Attendance,Long>{ long countByGymIdAndDate(Long gymId, LocalDate date); long countByDate(LocalDate date); List<Attendance> findByDateOrderByIdDesc(LocalDate date); }
