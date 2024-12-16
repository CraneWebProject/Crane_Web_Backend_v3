package crane.boardservice.dto;

import crane.boardservice.entity.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReplyResponseDto {
    private String content;

    private LocalDate createAt;

    private LocalDate updateAt;

    private Long userId;

    private Long boardId;

    @Builder
    public ReplyResponseDto (String content, LocalDate createAt, LocalDate updateAt, Long userId, Long boardId) {
        this.content = content;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.userId = userId;
        this.boardId = boardId;
    }

    public static ReplyResponseDto from(Reply reply){
        return ReplyResponseDto.builder()
                .content(reply.getContent())
                .createAt(reply.getCreatedAt())
                .updateAt(reply.getUpdatedAt())
                .userId(reply.getUserId())
                .boardId(reply.getBoardId())
                .build();
    }
}
