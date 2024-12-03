package sch.crane.domain.board.repository;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.board.exception.BoardNotFoundException;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findById(Long board_id);

    Page<Board> findByUser(Long user_id);

    Page<Board> findAllByPage(Pageable pageable);

    default Board findByIdOrElseThrow(Long boardId){
        return findById(boardId).orElseThrow(()-> new BoardNotFoundException("Board Id "+boardId+" Not Found"));
    }
}
