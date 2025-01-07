package crane.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "fcm_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_token", columnNames = {"user_id", "fcm_token"})
        }
)
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long FcmTokenId;

    private String fcmToken;

    private Long userId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime laseUsedAt;

    private String deviceInfo;

    private Boolean isActive;

    public void updateFcmToken(String fcmToken, String deviceInfo, Boolean isActive, LocalDateTime laseUsedAt) {
        this.fcmToken = fcmToken;
        this.deviceInfo = deviceInfo;
        this.isActive = isActive;
        this.laseUsedAt = laseUsedAt;
    }


    @Builder
    public FcmToken(String fcmToken, Long userId, String deviceInfo, Boolean isActive, LocalDateTime laseUsedAt) {
        this.fcmToken = fcmToken;
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.isActive = isActive;
        this.laseUsedAt = laseUsedAt;
    }
}
