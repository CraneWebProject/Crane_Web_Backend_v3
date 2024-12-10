package sch.crane.domain.reply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ReplyRequestDto {
    private String content;

    private Long userId;

    private Long boardId;

    public ReplyRequestDto(String content, Long boardId) {
        this.content = content;
        this.boardId = boardId;
    }

}
