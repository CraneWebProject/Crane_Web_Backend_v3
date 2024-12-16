package crane.boardservice.repository;

import crane.boardservice.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findById(Long replyId);

    @Query("SELECT r FROM Reply r WHERE r.boardId = :boardId")
    Page<Reply> findByBoardId(Long boardId, Pageable pageable);

    default Reply findByReplyIdOrElseThrow(Long replyId) {
        return this.findById(replyId).orElseThrow(()-> new IllegalArgumentException("Reply not found"));
    }
}
