package crane.teamservice.repository;

import crane.teamservice.entity.Team;
import crane.teamservice.entity.enums.TeamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findTeamByTeamId(Long teamId);

    Optional<Team> findTeamByTeamName(String teamName);
    List<Optional<Team>> findTeamByTeamType(TeamType teamType);

}
