package sch.crane.domain.team.entity;


import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import sch.crane.domain.team.entity.enums.TeamType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
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
    public Team(String name, LocalDateTime created_at, LocalDateTime updated_at, TeamType teamType) {
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.teamType = teamType;
    }
}
