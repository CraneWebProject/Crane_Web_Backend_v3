package sch.crane.domain.board.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sch.crane.domain.board.dto.BoardRequestDto;
import sch.crane.domain.board.dto.BoardResponseDto;
import sch.crane.domain.board.service.BoardService;
import sch.crane.domain.common.advice.ApiResponse;
import sch.crane.domain.common.dto.CustomUserDetails;
import sch.crane.domain.reply.service.ReplyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoards(@PathVariable Pageable pageable){
        Page<BoardResponseDto> boardResponseDtoList = boardService.readBoardsList(pageable);
        return ResponseEntity.ok(ApiResponse.success("게시판 불러오기 완료", boardResponseDtoList));
    }

    @PostMapping("/write")
    public ResponseEntity<ApiResponse<BoardResponseDto>> writeBoard(@AuthenticationPrincipal CustomUserDetails authUser, @RequestBody BoardRequestDto boardRequestDto){
        BoardResponseDto board = boardService.createBoard(boardRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("게시글 작성 완료", board));
    }

    @GetMapping("/{board_id}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> getBoardByBoardId(@PathVariable Long board_id){
        BoardResponseDto boardResponseDto = boardService.readBoard(board_id);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 완료", boardResponseDto));
    }

    @PutMapping("/{board_id}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(@AuthenticationPrincipal CustomUserDetails authUser, @RequestBody Long board_id, BoardRequestDto boardRequestDto){
        BoardResponseDto boardResponseDto = boardService.updateBoard(board_id, boardRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료", boardResponseDto));
    }

    @DeleteMapping("/{board_id}")
    public ResponseEntity<ApiResponse<String>> deleteBoard(@PathVariable Long board_id,Pageable pageable,@AuthenticationPrincipal CustomUserDetails authUser ){
        replyService.deleteReplyByBoard(board_id, pageable);
        boardService.deleteBoard(board_id, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("게시글, 댓글 삭제 완료"));
    } // 보드 삭제하면서 댓글도 같이 삭제하는걸 보드 컨트롤러에 구현하는게 맞나

    @GetMapping("/userBoards")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoardsByUserId(@RequestBody Pageable pageable, Long user_id){
        Page<BoardResponseDto> boardResponseDtoPage = boardService.readBoardsByUser(pageable, user_id);
        return ResponseEntity.ok(ApiResponse.success("유저 게시글 목록 조회 완료", boardResponseDtoPage));
    }
}
