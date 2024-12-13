package crane.teamservice.dto;

import crane.teamservice.entity.enums.TeamType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TeamRequestDto {
    private String teamName;

    private TeamType teamType;

    public TeamRequestDto(String teamName, TeamType teamType) {
        this.teamName = teamName;
        this.teamType = teamType;
    }
}
