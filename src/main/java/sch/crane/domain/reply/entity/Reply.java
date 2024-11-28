package sch.crane.domain.reply.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reply_id;

    private String content;

    @Timestamp
    @CreatedDate
    private LocalDate createAt;

    @Timestamp
    @LastModifiedDate
    private LocalDate updateAt;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @Builder
    public Reply(String content, User user) {
        this.content = content;
        this.user = user;
    }

    public void updateReply(String content) {
        this.content = content;
    }





}
