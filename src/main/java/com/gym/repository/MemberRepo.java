public interface MemberRepo extends JpaRepository<Member,Long>{ Optional<Member> findByUserId(Long userId); Optional<Member> findByUserIdAndGymId(Long userId,Long gymId); }
