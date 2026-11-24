package com.gym.repository;
import com.gym.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
List<Attendance> findByMemberId(Long memberId);
List<Attendance> findByDate(String date);
}
