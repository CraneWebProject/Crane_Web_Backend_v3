package crane.teamservice.entity;


import crane.teamservice.entity.enums.TeamType;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    private String teamName;

    @Timestamp
    @CreatedDate
    private LocalDateTime createdAt;

    @Timestamp
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    @Builder
    public Team(String teamName, TeamType teamType) {
        this.teamName = teamName;
        this.teamType = teamType;
    }

    public void updateTeam(String teamName, TeamType teamType) {
        this.teamName = teamName;
        this.teamType = teamType;
    }

}
