package crane.teamservice.dto;

import crane.teamservice.entity.Team;
import crane.teamservice.entity.enums.TeamType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TeamResponseDto {

    private Long teamId;

    private String teamName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private TeamType teamType;

    @Builder
    private TeamResponseDto(Long teamId,String teamName, LocalDateTime createdAt, LocalDateTime updatedAt, TeamType teamType) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.teamType = teamType;
    }

    public static TeamResponseDto from(Team team){
        return TeamResponseDto.builder()
                .teamId(team.getTeamId())
                .teamName(team.getTeamName())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .teamType(team.getTeamType())
                .build();
    }

}
