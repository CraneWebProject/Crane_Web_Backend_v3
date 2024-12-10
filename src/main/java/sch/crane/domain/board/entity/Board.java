package sch.crane.domain.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sch.crane.domain.board.entity.enums.BoardCategory;
import sch.crane.domain.reply.entity.Reply;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long board_id;

    private String title;

    //@Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer view = 0;

    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    private String attachFile;

    @CreatedDate
    private LocalDate createAt;

    @LastModifiedDate
    private LocalDate updateAt;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();


    @Builder
    public Board(String title, String content, BoardCategory boardCategory, String attachFile, User user) {
        this.title = title;
        this.content = content;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
        this.user = user;
    }

    public void updateBoard(String title, String content, BoardCategory boardCategory, String attachFile) {
        this.title = title;
        this.content = content;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
    }
}
