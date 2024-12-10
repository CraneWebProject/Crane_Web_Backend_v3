package sch.crane.domain.board.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.board.entity.enums.BoardCategory;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private String title;

    private String content;

    private Integer view;

    private BoardCategory boardCategory;

    private String attachFile;

    private LocalDate createAt;

    private LocalDate updateAt;

    private User user;

    @Builder
    public BoardResponseDto(String title, String content, Integer view, BoardCategory boardCategory, String attachFile, LocalDate createAt, LocalDate updateAt, User user) {
        this.title = title;
        this.content = content;
        this.view = view;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.user = user;
    }

    public static BoardResponseDto from(Board board){
        return BoardResponseDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .view(board.getView())
                .boardCategory(board.getBoardCategory())
                .attachFile(board.getAttachFile())
                .createAt(board.getCreateAt())
                .updateAt(board.getUpdateAt())
                .user(board.getUser())
                .build();
    }
}
