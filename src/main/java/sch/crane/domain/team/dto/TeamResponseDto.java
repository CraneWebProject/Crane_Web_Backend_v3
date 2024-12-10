package sch.crane.domain.team.dto;

import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.team.entity.Team;
import sch.crane.domain.team.entity.enums.TeamType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TeamResponseDto {

    private Long team_id;
    private String name;
    private TeamType teamType;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @Builder
    public TeamResponseDto(Long team_id, String name, TeamType teamType, LocalDateTime created_at, LocalDateTime updated_at) {
        this.team_id = team_id;
        this.name = name;
        this.teamType = teamType;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public static TeamResponseDto from(Team team) {
        return TeamResponseDto.builder()
                .team_id(team.getTeam_id())
                .name(team.getName())
                .teamType(team.getTeamType())
                .created_at(team.getCreated_at())
                .updated_at(team.getUpdated_at())
                .build();
    }

}
