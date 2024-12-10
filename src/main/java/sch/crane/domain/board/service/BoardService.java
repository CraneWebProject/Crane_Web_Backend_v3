package sch.crane.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sch.crane.domain.board.dto.BoardRequestDto;
import sch.crane.domain.board.dto.BoardResponseDto;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.board.repository.BoardRepository;
import sch.crane.domain.common.exception.BadRequestException;
import sch.crane.domain.reply.repository.ReplyRepository;
import sch.crane.domain.user.entity.User;
import sch.crane.domain.user.entity.enums.UserRole;
import sch.crane.domain.user.exception.UserNotFoundException;
import sch.crane.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto, String email){
        User user = userRepository.findByEmailOrElseThrow(email);
        Board board = Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .boardCategory(boardRequestDto.getBoardCategory())
                .attachFile(boardRequestDto.getAttachFile())
                .user(user)
                .build();
        boardRepository.save(board);
        return BoardResponseDto.from(board);
    } // 이메일로 유저 가져오고 유저 그대로 넣기

    public BoardResponseDto readBoard(Long boardId){
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        return BoardResponseDto.from(board);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto boardRequestDto, String email){
        User user = userRepository.findByEmailOrElseThrow(email);
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        if(!user.getUser_id().equals(board.getUser().getUser_id())){
            throw new BadRequestException("작성자만 수정 가능");
        }
        board.updateBoard(boardRequestDto.getTitle(),boardRequestDto.getContent(), boardRequestDto.getBoardCategory(), boardRequestDto.getAttachFile() );
        return BoardResponseDto.from(board);
    }

    @Transactional
    public BoardResponseDto deleteBoard(Long boardId, String email){
        User user = userRepository.findByEmailOrElseThrow(email);
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        if(!user.getUser_id().equals(board.getUser().getUser_id())&&
            !(user.getUserRole() == UserRole.ADMIN )){
            throw new BadRequestException("작성자와 관리자만 수정 가능");
        }
        boardRepository.deleteById(boardId); // Cascade.Tpye으로 인해 reply도 삭제가 됩니다
        return BoardResponseDto.from(board);
    }

    public Page<BoardResponseDto> readBoardsList(Pageable pageable) {
        Page<Board> boardList = boardRepository.findAll(pageable);

        return boardList.map(BoardResponseDto::from);
    }

    public Page<BoardResponseDto> readBoardsByUser(Pageable pageable, Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new UserNotFoundException("유저가 존재하지 않습니다."));
        Page<Board> boardList = boardRepository.findByUser(userId, pageable);

        return boardList.map(BoardResponseDto::from);
        // 이메일로 유저 검색 -> 유저 아이디 가져오기 -> 아이디로 게시글 가져오기
    }
}
