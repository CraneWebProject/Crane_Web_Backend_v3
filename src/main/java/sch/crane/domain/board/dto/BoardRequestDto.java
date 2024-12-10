package sch.crane.domain.board.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.board.entity.enums.BoardCategory;
import sch.crane.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BoardRequestDto {

    private String title;

    //@Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer view;

    private BoardCategory boardCategory;

    private String attachFile;

    private User user;

    public BoardRequestDto(String title, String content, Integer view, BoardCategory boardCategory, String attachFile, User user) {
        this.title = title;
        this.content = content;
        this.view = view;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
        this.user = user;
    }
}
