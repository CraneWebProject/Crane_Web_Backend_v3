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
    private Long teamMemberId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Timestamp
    @CreatedDate
    private LocalDateTime createdAt;

    private Long userId;

    //@JoinColumn(name = "team_id")
    //@ManyToOne(fetch = FetchType.LAZY)
    private Long teamId; // team_id로 바꿔놨는데 Team team으로 가져오는게 더 나을까요.

    @Builder
    public TeamMember(Role role, Long userId, Long teamId) {
        this.role = role;
        this.userId = userId;
        this.teamId = teamId;
    }
}
