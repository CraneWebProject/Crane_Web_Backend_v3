package sch.crane.domain.reply.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.board.repository.BoardRepository;
import sch.crane.domain.common.exception.BadRequestException;
import sch.crane.domain.reply.dto.ReplyRequestDto;
import sch.crane.domain.reply.dto.ReplyResponseDto;
import sch.crane.domain.reply.entity.Reply;
import sch.crane.domain.reply.repository.ReplyRepository;
import sch.crane.domain.user.entity.User;
import sch.crane.domain.user.entity.enums.UserRole;
import sch.crane.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReplyResponseDto createReply(ReplyRequestDto replyRequestDto, String email){
        User user = userRepository.findByEmailOrElseThrow(email);
        Board board = boardRepository.findByIdOrElseThrow(replyRequestDto.getBoardId());
        Reply reply = Reply.builder()
                .content(replyRequestDto.getContent())
                .user(user)
                .board(board)
                .build();
        replyRepository.save(reply);
        return ReplyResponseDto.from(reply);
    }

    public ReplyResponseDto readReply(Long replyId){
        Reply reply = replyRepository.findByIdOrElseThrow(replyId);
        return ReplyResponseDto.from(reply);
    }

    public Page<ReplyResponseDto> readBoardReplys(Long boardId, Pageable pageable){
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        Page<Reply> replyList = replyRepository.findByBoardId(board.getBoard_id(), pageable);
        return replyList.map(ReplyResponseDto::from);
    }

    @Transactional
    public ReplyResponseDto updateReply(Long replyId, ReplyRequestDto replyRequestDto, String email){
        User user = userRepository.findByEmailOrElseThrow(email);
        Reply reply = replyRepository.findByIdOrElseThrow(replyId);
        if(!user.getUser_id().equals(reply.getUser().getUser_id())){
            throw new BadRequestException("작성자만 수정 가능");
        }
        reply.updateReply(replyRequestDto.getContent());
        return ReplyResponseDto.from(reply);
    }

    @Transactional
    public ReplyResponseDto deleteReply(Long replyId, String email){
        User user = userRepository.findByEmailOrElseThrow(email);
        Reply reply = replyRepository.findByIdOrElseThrow(replyId);
        if(!user.getUser_id().equals(reply.getUser().getUser_id())&&
                !(user.getUserRole() == UserRole.ADMIN )){
            throw new BadRequestException("작성자와 관리자만 삭제 가능");
        }
        replyRepository.deleteById(replyId);
        return ReplyResponseDto.from(reply);
    }

    @Transactional
    public Page<ReplyResponseDto> deleteReplyByBoard(Long boardId, Pageable pageable){
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        Page<Reply> replyList = replyRepository.findByBoardId(board.getBoard_id(), pageable);
        replyList.forEach(reply -> replyRepository.deleteById(reply.getReply_id()));
        return replyList.map(ReplyResponseDto::from);
    }
}
