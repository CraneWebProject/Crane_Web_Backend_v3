package sch.crane.domain.reply.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reply_id;

    private String content;

    @CreatedDate
    private LocalDate createAt;

    @LastModifiedDate
    private LocalDate updateAt;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY) ///
    @JsonIgnore
    private User user;

    @JoinColumn(name = "board_id")
    @ManyToOne(fetch = FetchType.LAZY) ///
    @JsonIgnore
    private Board board;

    @Builder
    public Reply(String content, User user, Board board) {
        this.content = content;
        this.user = user;
        this.board = board;
    }

    public void updateReply(String content) {
        this.content = content;
    }





}
