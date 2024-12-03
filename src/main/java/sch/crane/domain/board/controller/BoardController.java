package sch.crane.domain.board.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sch.crane.domain.board.dto.BoardRequestDto;
import sch.crane.domain.board.dto.BoardResponseDto;
import sch.crane.domain.board.service.BoardService;
import sch.crane.domain.common.advice.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;
    @PostMapping
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoards(@RequestBody Pageable pageable){
        Page<BoardResponseDto> boardResponseDtoList = boardService.readBoardsList(pageable);
        return ResponseEntity.ok(ApiResponse.success("게시판 불러오기 완료", boardResponseDtoList));
    }

    @PostMapping("/write")
    public ResponseEntity<ApiResponse<BoardResponseDto>> writeBoard(@RequestBody BoardRequestDto boardRequestDto){
        BoardResponseDto board = boardService.createBoard(boardRequestDto);
        return ResponseEntity.ok(ApiResponse.success("게시글 작성 완료", board));
    }

    @GetMapping("/{board_Id}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> getBoardByBoardId(@RequestBody Long boardId){
        BoardResponseDto boardResponseDto = boardService.readBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 완료", boardResponseDto));
    }

    @PutMapping("/{board_id}")
    public ResponseEntity<ApiResponse<BoardResponseDto>> updateBoard(@RequestBody Long boardId, BoardRequestDto boardRequestDto){
        BoardResponseDto boardResponseDto = boardService.updateBoard(boardId, boardRequestDto);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료", boardResponseDto));

    }

    @DeleteMapping("/{board_id}")
    public ResponseEntity<ApiResponse<String>> deleteBoard(@RequestBody Long boardId){
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 완료"));
    }

    @GetMapping("/userBoards")
    public ResponseEntity<ApiResponse<Page<BoardResponseDto>>> getBoardsByUserId(@RequestBody Pageable pageable, Long userId){
        Page<BoardResponseDto> boardResponseDtoPage = boardService.readBoardsByUser(pageable, userId);
        return ResponseEntity.ok(ApiResponse.success("유저 게시글 목록 조회 완료", boardResponseDtoPage));
    }
}
