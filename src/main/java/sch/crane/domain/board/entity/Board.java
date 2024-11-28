package sch.crane.domain.board.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import sch.crane.domain.board.entity.enums.BoardCategory;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long board_id;

    private String title;

    private String content;

    private Integer view;

    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    private String attachFile;

    @Timestamp
    @CreatedDate
    private LocalDate createAt;

    @Timestamp
    @LastModifiedDate
    private LocalDate updateAt;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Board(String title, String content, BoardCategory boardCategory, String attachFile) {
        this.title = title;
        this.content = content;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
    }

    public void updateBoard(String title, String content, BoardCategory boardCategory, String attachFile) {
        this.title = title;
        this.content = content;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
    }
}
