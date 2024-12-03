package sch.crane.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sch.crane.domain.board.dto.BoardRequestDto;
import sch.crane.domain.board.dto.BoardResponseDto;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.board.repository.BoardRepository;
import sch.crane.domain.user.entity.User;
import sch.crane.domain.user.exception.UserNotFoundException;
import sch.crane.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto){
        Board board = Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .boardCategory(boardRequestDto.getBoardCategory())
                .attachFile(boardRequestDto.getAttachFile())
                .user(boardRequestDto.getUser())
                .build();
        boardRepository.save(board);
        return BoardResponseDto.from(board);
    }

    @Transactional
    public BoardResponseDto readBoard(Long boardId){
        Board board = boardRepository.findByIdOrElseThrow(boardId);

        return BoardResponseDto.from(board);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto boardRequestDto){
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        board.updateBoard(boardRequestDto.getTitle(),boardRequestDto.getContent(), boardRequestDto.getBoardCategory(), boardRequestDto.getAttachFile() );

        return BoardResponseDto.from(board);
    }

    @Transactional
    public boolean deleteBoard(Long boardId){
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        boardRepository.delete(board);
        return true;
    }

    @Transactional
    public Page<BoardResponseDto> readBoardsList(Pageable pageable) {
        Page<Board> boardList = boardRepository.findAllByPage(pageable);

        return boardList.map(BoardResponseDto::from);
    }

    @Transactional
    public Page<BoardResponseDto> readBoardsByUser(Pageable pageable, Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new UserNotFoundException("유저가 존재하지 않습니다."));
        Page<Board> boardList = boardRepository.findByUser(userId);

        return boardList.map(BoardResponseDto::from);
    }
}
