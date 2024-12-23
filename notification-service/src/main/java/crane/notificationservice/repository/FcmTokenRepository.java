package crane.notificationservice.repository;

import crane.notificationservice.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByUserIdAndFcmToken(Long userId, String fcmToken);

    Optional<FcmToken> findLatestByUserId(Long userId);

    boolean existsByFcmToken(String fcmToken);
}
