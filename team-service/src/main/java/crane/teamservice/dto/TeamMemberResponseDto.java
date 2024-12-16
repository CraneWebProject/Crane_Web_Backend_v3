package crane.teamservice.dto;

import crane.teamservice.entity.Team;
import crane.teamservice.entity.TeamMember;
import crane.teamservice.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class TeamMemberResponseDto {
    private Role role;

    private Long userId;

    private LocalDateTime createdAt;

    private Long teamId;

    @Builder
    public TeamMemberResponseDto(Role role, Long userId, LocalDateTime createdAt, Long teamId) {
        this.role = role;
        this.userId = userId;
        this.createdAt = createdAt;
        this.teamId = teamId;
    }

    public static TeamMemberResponseDto from(TeamMember teamMember){
        return TeamMemberResponseDto.builder()
                .role(teamMember.getRole())
                .userId(teamMember.getUserId())
                .createdAt(teamMember.getCreatedAt())
                .teamId(teamMember.getTeamId())
                .build();
    }
}
