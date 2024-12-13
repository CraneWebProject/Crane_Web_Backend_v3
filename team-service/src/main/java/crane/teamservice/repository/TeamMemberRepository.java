package crane.teamservice.repository;

import crane.teamservice.entity.TeamMember;
import crane.teamservice.entity.enums.Role;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamMemberId(Long teamMemberId);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.teamId = :teamId AND tm.role = :role")
    Optional<TeamMember> findTeamLeaderByTeamId(@Param("teamId") Long teamId, @Param("role") Role role);
    // queryDSL을 써야할것만 같아요. 이런 코드로 괜찮읅가요

    List<TeamMember> findByTeamId(Long teamId);

    Optional<TeamMember> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM TeamMember t WHERE t.teamId = :teamId")
    void deleteByTeamId(@Param("teamId") Long teamId);
}
