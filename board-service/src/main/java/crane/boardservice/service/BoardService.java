package crane.boardservice.service;

import crane.boardservice.client.UserClient;
import crane.boardservice.common.exceptions.BadRequestException;
import crane.boardservice.common.exceptions.NotFoundException;
import crane.boardservice.dto.BoardRequestDto;
import crane.boardservice.dto.BoardResponseDto;
import crane.boardservice.dto.UserResponseDto;
import crane.boardservice.entity.Board;
import crane.boardservice.repository.BoardRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserClient userClient;

    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto,Long userId) {
        UserResponseDto user = userClient.getById(userId).getData();
        Board board = Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .boardCategory(boardRequestDto.getBoardCategory())
                .attachFile(boardRequestDto.getAttachFile())
                .writer(user.getName())
                .userId(userId)
                .build();
        boardRepository.save(board);
        return BoardResponseDto.from(board);
    }

    // 단일 게시글 조회
    public BoardResponseDto readBoard(Long boardId){
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        return BoardResponseDto.from(board);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto boardRequestDto, Long userId){
        UserResponseDto user = userClient.getById(userId).getData();
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        if(!userId.equals(board.getUserId())){
            throw new BadRequestException("작성자만 수정 가능");
        }
        board.updateBoard(boardRequestDto.getTitle(),boardRequestDto.getContent(), boardRequestDto.getBoardCategory(), boardRequestDto.getAttachFile() );
        return BoardResponseDto.from(board);
    }


    // 게시글 삭제
    @Transactional
    public BoardResponseDto deleteBoard(Long boardId, Long userId){
        UserResponseDto user = userClient.getById(userId).getData();
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        if(userId.equals(board.getUserId())&&
                !(user.getUserRole().equals("ADMIN"))){
            throw new BadRequestException("작성자와 관리자만 수정 가능");
        }
        boardRepository.deleteById(boardId); // Cascade.Tpye으로 인해 reply도 삭제가 됩니다
        return BoardResponseDto.from(board);
    }

    // 전체 게시글 조회
    public Page<BoardResponseDto> readBoardsList(Pageable pageable) {
        Page<Board> boardList = boardRepository.findAll(pageable);

        return boardList.map(BoardResponseDto::from);
    }

    // 사용자 게시글 조회
    public Page<BoardResponseDto> readBoardsByUser(Pageable pageable, Long userId){
        UserResponseDto user = userClient.getById(userId).getData();

        Page<Board> boardList = boardRepository.findByUserId(userId, pageable);

        return boardList.map(BoardResponseDto::from);
    }

    // 제목 검색어로 게시글 조회
    public Page<BoardResponseDto> searchBoardsByTitle(String keyword, Pageable pageable) {
        Page<Board> boards = boardRepository.findByTitleContaining(keyword, pageable);
        return boards.map(BoardResponseDto::from);
    }

    // 질문이 있습니다. 지금은 userClient에서 userId로 User 정보를 가지고 옴. userController에서 만들어진 API만 가져올 수 있음.
     // 만약에 사용자 이름으로 그 사람이 쓴 보드를 검색하는 기능을 만들고 싶으면 어떻게 해야되죠.



}
