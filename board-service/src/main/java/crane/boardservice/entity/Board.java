package crane.boardservice.entity;

import crane.boardservice.entity.enums.BoardCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.lang.management.LockInfo;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private Long userId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer view=0;

    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    private String attachFile;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String writer;


    @Builder
    public Board(String title, String content, BoardCategory boardCategory, String attachFile, String writer,Long userId) {
        this.title = title;
        this.content = content;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
        this.writer = writer;
        this.userId = userId;
    }

    public void updateBoard(String title, String content, BoardCategory boardCategory, String attachFile) {
        this.title = title;
        this.content = content;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
    }

}
