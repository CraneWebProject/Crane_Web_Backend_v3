package sch.crane.domain.reply.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sch.crane.domain.reply.entity.Reply;
import sch.crane.domain.reply.exception.ReplyNotExistException;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findById(Long reply_id);

    @Query("SELECT r FROM Reply r WHERE r.board.board_id = :boardId")
    Page<Reply> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    default Reply findByIdOrElseThrow(Long replyId){
        return findById(replyId).orElseThrow(()-> new ReplyNotExistException("Reply Id "+replyId+" Not Found"));
    }

}
