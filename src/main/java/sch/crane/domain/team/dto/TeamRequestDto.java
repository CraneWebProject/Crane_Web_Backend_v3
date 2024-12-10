package sch.crane.domain.team.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.team.entity.enums.TeamType;

@Getter
@NoArgsConstructor
public class TeamRequestDto {

    private Long team_id;
    private String teamName;
    private TeamType teamType;

    public TeamRequestDto(Long team_id,String teamName, TeamType teamType) {
        this.team_id = team_id;
        this.teamName = teamName;
        this.teamType = teamType;
    }
}
