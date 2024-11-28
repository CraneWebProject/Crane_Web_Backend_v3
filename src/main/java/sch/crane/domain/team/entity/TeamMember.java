package sch.crane.domain.team.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import sch.crane.domain.team.entity.enums.Role;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamMember_id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Timestamp
    @CreatedDate
    private LocalDate joinAt;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;


    public void updateTeamMember(Role role) {
        this.role = role;
    }

    @Builder
    public TeamMember(Role role, LocalDate joinAt, User user, Team team) {
        this.role = role;
        this.joinAt = joinAt;
        this.user = user;
        this.team = team;
    }
}
