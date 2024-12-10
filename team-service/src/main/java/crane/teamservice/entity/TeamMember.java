package crane.teamservice.entity;

import crane.teamservice.entity.enums.Role;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TeamMember_id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Timestamp
    @CreatedDate
    private LocalDateTime createdAt;

    private Long user_id;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Builder
    public TeamMember(Role role, Long user_id, Team team) {
        this.role = role;
        this.user_id = user_id;
        this.team = team;
    }
}
