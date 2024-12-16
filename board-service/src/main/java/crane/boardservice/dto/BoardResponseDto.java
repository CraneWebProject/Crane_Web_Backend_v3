package crane.boardservice.dto;

import crane.boardservice.entity.Board;
import crane.boardservice.entity.enums.BoardCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private String title;

    private String content;

    private Integer view;

    private BoardCategory boardCategory;

    private String attachFile;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private String writer;

    @Builder
    private BoardResponseDto (String title, String content, Integer view, BoardCategory boardCategory, String attachFile, LocalDate createdAt, LocalDate updatedAt, String writer) {
        this.title = title;
        this.content = content;
        this.view = view;
        this.boardCategory = boardCategory;
        this.attachFile = attachFile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.writer = writer;
    }

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .view(board.getView())
                .boardCategory(board.getBoardCategory())
                .attachFile(board.getAttachFile())
                .createdAt(board.getCreatedAt() != null ? LocalDate.from(board.getCreatedAt()) : null) // null 체크 추가
                .updatedAt(board.getUpdatedAt() != null ? LocalDate.from(board.getUpdatedAt()) : null) // null 체크 추가
                .writer(board.getWriter())
                .build();
    }
}
