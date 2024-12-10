package crane.boardservice.repository;

import crane.boardservice.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findById(Long reply_id);

   // Optional<Reply> findByBoardId(Long board_id);

    default Reply findByReplyIdOrElseThrow(Long reply_id) {
        return this.findById(reply_id).orElseThrow(()-> new IllegalArgumentException("Reply not found"));
    }
}
