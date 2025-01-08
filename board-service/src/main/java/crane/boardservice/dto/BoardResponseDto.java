package crane.boardservice.dto;

import crane.boardservice.entity.Board;
import crane.boardservice.entity.enums.BoardCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private Long boardId;

    private String title;

    private String content;

    private Integer view;

    private BoardCategory boardCategory;

    private String attachFile;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String writer;

    @Builder
    private BoardResponseDto (Long boardId, String title, String content, Integer view, BoardCategory boardCategory, String attachFile, LocalDateTime createdAt, LocalDateTime updatedAt, String writer) {
        this.boardId = boardId;
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
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .view(board.getView())
                .boardCategory(board.getBoardCategory())
                .attachFile(board.getAttachFile())
                .createdAt(board.getCreatedAt() != null ? LocalDateTime.from(board.getCreatedAt()) : null) // null 체크 추가
                .updatedAt(board.getUpdatedAt() != null ? LocalDateTime.from(board.getUpdatedAt()) : null) // null 체크 추가
                .writer(board.getWriter())
                .build();
    }
}
