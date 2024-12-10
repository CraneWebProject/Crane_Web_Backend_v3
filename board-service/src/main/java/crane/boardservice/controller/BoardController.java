package crane.boardservice.controller;

import crane.boardservice.common.advice.ApiResponse;
import crane.boardservice.dto.BoardRequestDto;
import crane.boardservice.dto.BoardResponseDto;
import crane.boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponseDto>> wirteBoard(@RequestHeader("X-Authenticated-User") Long userId,
                                                                    @RequestBody BoardRequestDto boardRequestDto) {
        BoardResponseDto board = boardService.createBoard(boardRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("작성 완료",board));
    }

}
