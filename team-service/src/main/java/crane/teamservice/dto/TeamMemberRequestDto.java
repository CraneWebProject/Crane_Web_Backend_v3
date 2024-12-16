package crane.teamservice.dto;

import crane.teamservice.entity.Team;
import crane.teamservice.entity.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamMemberRequestDto {
    private Role role;

    private Long userId;

    private Long teamId;

    public TeamMemberRequestDto(Role role, Long userId, Long teamId) {
        this.role = role;
        this.userId = userId;
        this.teamId = teamId;
    }
}
