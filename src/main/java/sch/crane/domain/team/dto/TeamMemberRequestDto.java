package sch.crane.domain.team.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.team.entity.Team;
import sch.crane.domain.team.entity.enums.Role;
import sch.crane.domain.user.entity.User;

@Getter
@NoArgsConstructor
public class TeamMemberRequestDto {
    private Long teamMember_id;
    private Role role;
    private Long user_id;
    private Long team_id;

    public TeamMemberRequestDto(Long teamMember_id, Role role, Long user_id, Long team_id) {
        this.teamMember_id = teamMember_id;
        this.role = role;
        this.user_id = user_id;
        this.team_id = team_id;
    }
}
