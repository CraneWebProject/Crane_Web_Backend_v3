package sch.crane.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sch.crane.domain.team.entity.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {


    default Team findByIdOrElseThrow(long team_id) {
        return findById(team_id).orElseThrow(() -> new RuntimeException("팀이 존재하지 않습니다"));
    }

}
