package sch.crane.domain.reply.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sch.crane.domain.common.advice.ApiResponse;
import sch.crane.domain.common.dto.CustomUserDetails;
import sch.crane.domain.reply.dto.ReplyRequestDto;
import sch.crane.domain.reply.dto.ReplyResponseDto;
import sch.crane.domain.reply.service.ReplyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replys")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReplyResponseDto>> writeReply(@AuthenticationPrincipal CustomUserDetails authUser, @RequestBody ReplyRequestDto replyRequestDto){
        ReplyResponseDto replyResponseDto = replyService.createReply(replyRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 완료", replyResponseDto));
    }

    @GetMapping("/{reply_id}")
    public ResponseEntity<ApiResponse<ReplyResponseDto>> readReply(@PathVariable Long reply_id){
        ReplyResponseDto replyResponseDto = replyService.readReply(reply_id);
        return ResponseEntity.ok(ApiResponse.success("댓글 조회 성공", replyResponseDto));
    }

    @PutMapping("/{reply_id}")
    public ResponseEntity<ApiResponse<ReplyResponseDto>> updateReply(@AuthenticationPrincipal CustomUserDetails authUser, @PathVariable Long reply_id, @RequestBody ReplyRequestDto replyRequestDto){
        ReplyResponseDto replyResponseDto = replyService.updateReply(reply_id, replyRequestDto, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("댓글 수정 완료", replyResponseDto));
    }

    @DeleteMapping("/{reply_id}")
    public ResponseEntity<ApiResponse<ReplyResponseDto>> deleteReply(@AuthenticationPrincipal CustomUserDetails authUser, @PathVariable Long reply_id){
        ReplyResponseDto replyResponseDto = replyService.deleteReply(reply_id, authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 완료", replyResponseDto));
    }

    @GetMapping("/boards/{board_id}")
    public ResponseEntity<ApiResponse<Page<ReplyResponseDto>>> readBoardReplys(@PathVariable Long board_id, @RequestParam(name = "createAt", required = false) Pageable pageable){
        Page<ReplyResponseDto> replyList = replyService.readBoardReplys(board_id, pageable);
        return ResponseEntity.ok(ApiResponse.success("보드 댓글 조회 완료", replyList));
    }
}
