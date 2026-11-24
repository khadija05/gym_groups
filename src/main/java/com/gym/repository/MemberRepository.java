package com.gym.repository;
import com.gym.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MemberRepository extends JpaRepository<Member, Long> {
List<Member> findByCreatedBy(Long createdBy);
List<Member> findByStatus(String status);
}
