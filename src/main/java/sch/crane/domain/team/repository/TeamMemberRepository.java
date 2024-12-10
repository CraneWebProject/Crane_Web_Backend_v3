package sch.crane.domain.team.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sch.crane.domain.team.entity.TeamMember;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    @Query("SELECT t FROM TeamMember t WHERE t.user.user_id = :user_id")
    List<TeamMember> findByUserId(@Param("user_id") Long user_id);

    @Query("SELECT t FROM TeamMember t WHERE t.team.team_id = :team_id")
    List<TeamMember> findByTeamId(@Param("team_id") Long team_id);

}
