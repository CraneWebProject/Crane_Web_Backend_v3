package sch.crane.domain.team.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.team.entity.Team;
import sch.crane.domain.team.entity.TeamMember;
import sch.crane.domain.team.entity.enums.Role;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TeamMemberResponseDto {
    private Role role;
    private LocalDate JoinAt;
    private User user;
    private Team team;

    @Builder
    public TeamMemberResponseDto(Role role, User user, Team team) {
        this.role = role;
        this.user = user;
        this.team = team;
    }

    public static TeamMemberResponseDto from(TeamMember teamMember) {
        return TeamMemberResponseDto.builder()
                .team(teamMember.getTeam())
                .user(teamMember.getUser())
                .role(teamMember.getRole())
                .build();
    }
}
