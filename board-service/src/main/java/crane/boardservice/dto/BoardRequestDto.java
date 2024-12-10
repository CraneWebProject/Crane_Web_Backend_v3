package crane.boardservice.dto;

import crane.boardservice.entity.Board;
import crane.boardservice.entity.enums.BoardCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
    private String title;

    private String content;

    private BoardCategory boardCategory;

    private String attachFile;


    public BoardRequestDto(String title, String content, BoardCategory boardCategory, String attachFile) {
        this.title = title;
        this.content = content;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;

    }

}
