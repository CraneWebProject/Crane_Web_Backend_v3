package crane.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;

    public void sendPushNotificationToUser(String userId, String title, String body) {
        try {
            // DB 또는 캐시에서 해당 userId의 FCM 토큰을 가져오는 로직 필요
            String fcmToken = getFcmTokenByUserId(userId); // 구현 필요

            if (fcmToken != null) {
                Message message = Message.builder()
                        .setToken(fcmToken)
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build();

                String response = firebaseMessaging.send(message);
                System.out.println("Successfully sent message: " + response);
            } else {
                System.out.println("FCM token not found for user: " + userId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFcmTokenByUserId(String userId) {
        // DB 또는 캐시에서 userId에 해당하는 FCM 토큰을 조회하는 로직 구현
        // 예시:
        // User user = userRepository.findByUserId(userId);
        // return user != null ? user.getFcmToken() : null;

        // 임시로 하드코딩
        if (userId.equals("user123")){
            return "YOUR_FCM_TOKEN";
        }
        return null;
    }
}
