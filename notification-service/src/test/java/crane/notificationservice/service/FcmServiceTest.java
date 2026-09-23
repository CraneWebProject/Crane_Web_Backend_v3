package crane.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import crane.notificationservice.entity.FcmToken;
import crane.notificationservice.repository.FcmTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FcmServiceTest {

    @Mock
    FirebaseMessaging firebaseMessaging;
    @Mock
    FcmTokenRepository fcmTokenRepository;
    @InjectMocks
    FcmService fcmService;

    @Test
    void throwsWhenTokenMissing() {
        when(fcmTokenRepository.findLatestByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> fcmService.sendPushNotificationToUser(1L, "title", "body"));
        verifyNoInteractions(firebaseMessaging);
    }

    @Test
    void propagatesSendFailure() throws Exception {
        when(fcmTokenRepository.findLatestByUserId(1L))
                .thenReturn(Optional.of(FcmToken.builder().fcmToken("token").userId(1L).build()));
        when(firebaseMessaging.send(any(Message.class))).thenThrow(mock(FirebaseMessagingException.class));

        assertThrows(FirebaseMessagingException.class,
                () -> fcmService.sendPushNotificationToUser(1L, "title", "body"));
    }
}
