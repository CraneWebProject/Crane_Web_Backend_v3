package crane.boardservice.service;

import crane.boardservice.client.UserClient;
import crane.boardservice.common.exceptions.BadRequestException;
import crane.boardservice.dto.BoardRequestDto;
import crane.boardservice.dto.BoardResponseDto;
import crane.boardservice.dto.UserResponseDto;
import crane.boardservice.entity.Board;
import crane.boardservice.entity.enums.BoardCategory;
import crane.boardservice.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserClient userClient;

    private final RedisTemplate<String, String> redisTemplate;

    // 게시글 작성
    @Caching(evict = {
            @CacheEvict(value = "boardByUser", key = "#userId"),
            @CacheEvict(value = "boardByCategory", key = "#boardRequestDto.boardCategory")
    })
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
    @Cacheable(value = "boardById", key = "#boardId", cacheManager = "redisCacheManager")
    public BoardResponseDto readBoard(Long boardId){
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        return BoardResponseDto.from(board);
    }

    // 전체 게시글 조회
    public Page<BoardResponseDto> readAllBoard(Pageable pageable){
        Page<Board> boardPage = boardRepository.findAll(pageable);
        return boardPage.map(BoardResponseDto::from);
    }

    // 게시글 수정
    @Caching(evict = {
            @CacheEvict(value = "boardById", key = "#boardId"),
            @CacheEvict(value = "boardByUser", key = "#userId"),
            @CacheEvict(value = "boardByCategory", key = "#boardRequestDto.boardCategory")
    })
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
    @Caching(evict = {
            @CacheEvict(value = "boardById", key = "#boardId"),
            @CacheEvict(value = "boardByUser", key = "#userId")
    })
    @Transactional
    public BoardResponseDto deleteBoard(Long boardId, Long userId){
        UserResponseDto user = userClient.getById(userId).getData();
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        if(userId.equals(board.getUserId())&&
                !(user.getUserRole().equals("ADMIN"))){
            throw new BadRequestException("작성자와 관리자만 수정 가능");
        }
        boardRepository.deleteById(boardId);

        return BoardResponseDto.from(board);
    }

    // 카테고리 별 게시글 조회
    @Cacheable(value = "boardByCategory", key = "#boardCategory", cacheManager = "redisCacheManager")
    public Page<BoardResponseDto> readBoardsList(BoardCategory boardCategory, Pageable pageable) {
        Page<Board> boardList = boardRepository.findByBoardCategory(boardCategory, pageable);

        return boardList.map(BoardResponseDto::from);
    }

    // 사용자 게시글 조회
    @Cacheable(value = "boardByUser", key = "#userId", cacheManager = "redisCacheManager")
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

}
