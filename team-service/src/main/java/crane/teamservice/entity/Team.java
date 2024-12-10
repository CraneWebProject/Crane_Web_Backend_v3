package crane.teamservice.entity;


import crane.teamservice.entity.enums.TeamType;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bouncycastle.jcajce.provider.symmetric.TEA;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long team_id;

    private String name;

    @Timestamp
    @CreatedDate
    private LocalDateTime created_at;

    @Timestamp
    @LastModifiedDate
    private LocalDateTime updated_at;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    @Builder
    public Team(String name, TeamType teamType) {
        this.name = name;
        this.teamType = teamType;
    }

    public void updateTeam(String name, TeamType teamType) {
        this.name = name;
        this.teamType = teamType;
    }

}
