package crane.boardservice.repository;

import crane.boardservice.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findById(Long boardId);

    default Board findByBoardIdOrElseThrow(Long boardId) {
        return findById(boardId).orElseThrow(()-> new IllegalArgumentException("Board not found"));
    }

    @Query("SELECT b FROM Board b WHERE b.userId = :userId")
    Page<Board> findByUserId(Long userId, Pageable pageable);

    Page<Board> findByTitleContaining(String keyword, Pageable pageable);

}
