package crane.boardservice.controller;

import crane.boardservice.common.advice.ApiResponse;
import crane.boardservice.dto.BoardRequestDto;
import crane.boardservice.dto.BoardResponseDto;
import crane.boardservice.entity.enums.BoardCategory;
import crane.boardservice.service.BoardService;
import crane.boardservice.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;

    // 카테고리 게시글 조회
    @GetMapping("/category/{boardCategory}")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoardsByCategory(Pageable pageable, @PathVariable BoardCategory boardCategory){
        Page<BoardResponseDto> boardResponseDtoList = boardService.readBoardsList(boardCategory, pageable);
        return ResponseEntity.ok(ApiResponse.success(boardCategory + "카테고리 게시판 불러오기 완료", boardResponseDtoList));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getAllBoards(Pageable pageable){
        Page<BoardResponseDto> boardResponseDtoPage = boardService.readAllBoard(pageable);
        return ResponseEntity.ok(ApiResponse.success("전체 게시글 불러오기 완료", boardResponseDtoPage));
    }

    @PostMapping("/write")
    public ResponseEntity<ApiResponse<BoardResponseDto>> writeBoard(@RequestHeader("X-Authenticated-User") Long userId,
                                                                    @RequestBody BoardRequestDto boardRequestDto) {
        BoardResponseDto board = boardService.createBoard(boardRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("작성 완료",board));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> getBoardByBoardId(@PathVariable Long boardId){
        BoardResponseDto boardResponseDto = boardService.readBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 완료", boardResponseDto));
    }
    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(@RequestHeader("X-Authenticated-User") Long userId,@PathVariable Long boardId,  @RequestBody BoardRequestDto boardRequestDto ){
        BoardResponseDto boardResponseDto = boardService.updateBoard(boardId, boardRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료", boardResponseDto));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<String>> deleteBoard(@PathVariable Long boardId, Pageable pageable, @RequestHeader("X-Authenticated-User") Long userId){
        replyService.deleteReplyByBoard(boardId, pageable);
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글, 댓글 삭제 완료"));
    }

    @GetMapping("/userBoards")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoardsByUserId(Pageable pageable,@RequestHeader("X-Authenticated-User") Long userId){
        Page<BoardResponseDto> boardResponseDtoPage = boardService.readBoardsByUser(pageable, userId);
        return ResponseEntity.ok(ApiResponse.success("유저 게시글 목록 조회 완료", boardResponseDtoPage));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoardByTitleKeyword(@PathVariable String keyword, Pageable pageable){
        Page<BoardResponseDto> boardResponseDtoPage = boardService.searchBoardsByTitle(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("키워드 게시글 목록 조회 완료", boardResponseDtoPage));
    }
}
