// repository/AttendanceRepository.java
package com.gym.repository;
import com.gym.model.Attendance; import org.springframework.data.jpa.repository.JpaRepository;
public interface AttendanceRepository extends JpaRepository<Attendance,Long>{}
