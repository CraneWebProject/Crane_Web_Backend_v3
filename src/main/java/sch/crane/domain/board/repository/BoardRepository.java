package sch.crane.domain.board.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sch.crane.domain.board.entity.Board;
import sch.crane.domain.board.exception.BoardNotFoundException;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findById(Long board_id);
    @Query("SELECT b FROM Board b WHERE b.user.user_id = :user_id")
    Page<Board> findByUser(@Param("user_id")Long user_id, Pageable pageable);

    Page<Board> findAll(Pageable pageable);

    default Board findByIdOrElseThrow(Long board_id){
        return findById(board_id).orElseThrow(()-> new BoardNotFoundException("Board Id "+board_id+" Not Found"));
    }
}
