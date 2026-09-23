package crane.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import crane.notificationservice.entity.FcmToken;
import crane.notificationservice.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    // 실패는 호출자(Kafka 리스너)로 전파해 재시도 → DLT 경로를 타게 한다.
    public void sendPushNotificationToUser(Long userId, String title, String body) throws FirebaseMessagingException {
        String fcmToken = getFcmTokenByUserId(userId);

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        String response = firebaseMessaging.send(message);
        log.info("FCM 발송 성공 - userId: {}, response: {}", userId, response);
    }

    // 토큰 부재는 재시도해도 결과가 같으므로 재시도 제외 대상인 IllegalArgumentException 으로 던진다.
    private String getFcmTokenByUserId(Long userId) {
        Optional<FcmToken> optionalFcmToken =  fcmTokenRepository.findLatestByUserId(userId);
        return optionalFcmToken.map(FcmToken::getFcmToken)
                .orElseThrow(() -> new IllegalArgumentException("FCM token not found for user: " + userId));
    }
}
