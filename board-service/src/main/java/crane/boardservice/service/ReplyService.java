package crane.boardservice.service;

import crane.boardservice.client.UserClient;
import crane.boardservice.common.exceptions.BadRequestException;
import crane.boardservice.dto.ReplyRequestDto;
import crane.boardservice.dto.ReplyResponseDto;
import crane.boardservice.dto.UserResponseDto;
import crane.boardservice.entity.Board;
import crane.boardservice.entity.Reply;
import crane.boardservice.repository.BoardRepository;
import crane.boardservice.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserClient userClient;
    private final BoardRepository boardRepository;

    // 댓글 작성
    @CacheEvict(value = "replyByBoardId", key = "#replyRequestDto.boardId")
    @Transactional
    public ReplyResponseDto createReply(ReplyRequestDto replyRequestDto, Long userId){
        UserResponseDto user = userClient.getById(userId).getData();
        Board board = boardRepository.findByBoardIdOrElseThrow(replyRequestDto.getBoardId());
        Reply reply = Reply.builder()
                .content(replyRequestDto.getContent())
                .userId(userId)
                .boardId(board.getBoardId())
                .build();
        replyRepository.save(reply);
        return ReplyResponseDto.from(reply);
    }

    // 단일 댓글 조회
    public ReplyResponseDto readReply(Long replyId){
        Reply reply = replyRepository.findByReplyIdOrElseThrow(replyId);
        return ReplyResponseDto.from(reply);
    }

    // 게시글 전체 댓글 조회
    @Cacheable(value = "replyByBoardId", key = "#boardId", cacheManager = "redisCacheManager")
    public Page<ReplyResponseDto> readBoardReplys(Long boardId, Pageable pageable){
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        Page<Reply> replyList = replyRepository.findByBoardId(board.getBoardId(), pageable);
        return replyList.map(ReplyResponseDto::from);
    }

    // 댓글 수정
    @CacheEvict(value = "replyByBoardId", key = "#replyRequestDto.boardId")
    @Transactional
    public ReplyResponseDto updateReply(Long replyId, ReplyRequestDto replyRequestDto, Long userId){
        UserResponseDto user = userClient.getById(userId).getData();
        Reply reply = replyRepository.findByReplyIdOrElseThrow(replyId);
        if(userId.equals(reply.getUserId())){
            throw new BadRequestException("작성자만 수정 가능");
        }
        reply.updateReply(replyRequestDto.getContent());
        return ReplyResponseDto.from(reply);
    }

    // 댓글 삭제
    @CacheEvict(value = "replyByBoardId", key = "#result.boardId")
    @Transactional
    public ReplyResponseDto deleteReply(Long replyId, Long userId){
        UserResponseDto user = userClient.getById(userId).getData();
        Reply reply = replyRepository.findByReplyIdOrElseThrow(replyId);
        if(!userId.equals(reply.getUserId())&&
                !(user.getUserRole().equals("ADMIN") )){
            throw new BadRequestException("작성자와 관리자만 삭제 가능");
        }
        replyRepository.deleteById(replyId);
        return ReplyResponseDto.from(reply);
    }

    // 게시글 댓글 전체 삭제
    @Transactional
    public Page<ReplyResponseDto> deleteReplyByBoard(Long boardId, Pageable pageable){
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        Page<Reply> replyList = replyRepository.findByBoardId(board.getBoardId(), pageable);
        replyList.forEach(reply -> replyRepository.deleteById(reply.getReplyId()));
        return replyList.map(ReplyResponseDto::from);
    }

}
