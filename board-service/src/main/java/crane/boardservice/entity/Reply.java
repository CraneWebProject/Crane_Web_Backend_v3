package crane.boardservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reply_id;

    private String content;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    private Long user_id;

    private Long board_id;

    @Builder
    public Reply(String content, LocalDate createdAt, LocalDate updatedAt, Long user_id, Long board_id){
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user_id = user_id;
        this.board_id = board_id;
    }

    public void updateReply(String content){
        this.content = content;
    }
}
