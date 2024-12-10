package sch.crane.domain.reply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.reply.entity.Reply;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReplyResponseDto {

    private String content;

    private LocalDate createAt;

    private LocalDate updateAt;

    private String userName;

    private Long boardId;

    @Builder
    public ReplyResponseDto (String content, LocalDate createAt, LocalDate updateAt, String userName, Long boardId) {
        this.content = content;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.userName = userName;
        this.boardId = boardId;
    }

    public static ReplyResponseDto from(Reply reply){
        return ReplyResponseDto.builder()
                .content(reply.getContent())
                .createAt(reply.getCreateAt())
                .updateAt(reply.getUpdateAt())
                .userName(reply.getUser().getName())
                .boardId(reply.getBoard().getBoard_id())
                .build();
    }
}
