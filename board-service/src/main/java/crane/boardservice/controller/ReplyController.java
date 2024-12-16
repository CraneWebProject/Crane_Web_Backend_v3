package crane.boardservice.controller;

import crane.boardservice.client.ApiResponse;
import crane.boardservice.dto.ReplyRequestDto;
import crane.boardservice.dto.ReplyResponseDto;
import crane.boardservice.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replys")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReplyResponseDto>> writeReply(@RequestHeader("X-Authenticated-User") Long userId, @RequestBody ReplyRequestDto replyRequestDto){
        ReplyResponseDto replyResponseDto = replyService.createReply(replyRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 완료", replyResponseDto));
    }

    @GetMapping("/{replyId}")
    public ResponseEntity<ApiResponse<ReplyResponseDto>> readReply(@PathVariable Long replyId){
        ReplyResponseDto replyResponseDto = replyService.readReply(replyId);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", replyResponseDto));
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<ApiResponse<ReplyResponseDto>> updateReply(@RequestHeader("X-Authenticated-User") Long userId, @PathVariable Long replyId, @RequestBody ReplyRequestDto replyRequestDto){
        ReplyResponseDto replyResponseDto = replyService.updateReply(replyId, replyRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글 수정 완료", replyResponseDto));
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<ApiResponse<ReplyResponseDto>> deleteReply(@RequestHeader("X-Authenticated-User") Long userId, @PathVariable Long replyId){
        ReplyResponseDto replyResponseDto = replyService.deleteReply(replyId, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 완료", replyResponseDto));
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponse<Page<ReplyResponseDto>>> readBoardReplys(@PathVariable Long boardId, @RequestParam(name = "createAt", required = false) Pageable pageable){
        Page<ReplyResponseDto> replyList = replyService.readBoardReplys(boardId, pageable);
        return ResponseEntity.ok(ApiResponse.success("보드 댓글 조회 완료", replyList));
    }

}
