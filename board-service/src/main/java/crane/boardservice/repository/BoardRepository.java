package crane.boardservice.repository;

import crane.boardservice.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findById(Long board_id);

    default Board findByBoardIdOrElseThrow(Long board_id) {
        return findById(board_id).orElseThrow(()-> new IllegalArgumentException("Board not found"));
    }
}
