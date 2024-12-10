package crane.boardservice.service;

import crane.boardservice.client.UserClient;
import crane.boardservice.dto.BoardRequestDto;
import crane.boardservice.dto.BoardResponseDto;
import crane.boardservice.dto.UserResponseDto;
import crane.boardservice.entity.Board;
import crane.boardservice.repository.BoardRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
