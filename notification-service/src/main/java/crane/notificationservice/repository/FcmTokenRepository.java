package crane.notificationservice.repository;

import crane.notificationservice.entity.FcmToken;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    @Query("SELECT f FROM FcmToken f WHERE f.fcmToken = :fcmToken AND f.userId = :userId")
    Optional<FcmToken> findByUserIdAndFcmToken(@Param("userId") Long userId,
                                               @Param("fcmToken") String fcmToken);

    Optional<FcmToken> findLatestByUserId(Long userId);

    @Query("DELETE FROM FcmToken f WHERE f.fcmToken = :fcmToken AND f.userId = :userId")
    int deleteByFcmTokenAndUserId(@Param("userId") Long userId,
                                  @Param("fcmToken") String fcmToken);

    boolean existsByFcmToken(String fcmToken);
}
