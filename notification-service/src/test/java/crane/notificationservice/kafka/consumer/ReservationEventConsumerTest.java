package crane.notificationservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import crane.notificationservice.service.FcmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ReservationEventConsumerTest {

    private static final String MESSAGE =
            "{\"userId\":1,\"reservationStatus\":\"SUCCESS\",\"notificationMessage\":\"예약이 완료되었습니다.\"}";

    @Mock
    FcmService fcmService;
    @Mock
    Acknowledgment ack;

    ReservationEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ReservationEventConsumer(fcmService, new ObjectMapper());
    }

    @Test
    void acksOnlyAfterSuccess() throws Exception {
        consumer.listen(MESSAGE, 0, 0L, ack);

        verify(fcmService).sendPushNotificationToUser(1L, "SUCCESS", "예약이 완료되었습니다.");
        verify(ack).acknowledge();
    }

    @Test
    void propagatesParseFailureWithoutAck() {
        assertThrows(JsonProcessingException.class, () -> consumer.listen("not-json", 0, 0L, ack));

        verifyNoInteractions(fcmService, ack);
    }

    @Test
    void propagatesSendFailureWithoutAck() throws Exception {
        doThrow(mock(FirebaseMessagingException.class))
                .when(fcmService).sendPushNotificationToUser(any(), any(), any());

        assertThrows(FirebaseMessagingException.class, () -> consumer.listen(MESSAGE, 0, 0L, ack));
        verifyNoInteractions(ack);
    }

    @Test
    void dltListenerAcksOnSuccess() throws Exception {
        consumer.listenDlt(MESSAGE, ack);

        verify(ack).acknowledge();
    }

    @Test
    void dltListenerPropagatesFailureWithoutAck() throws Exception {
        doThrow(new IllegalArgumentException("FCM token not found for user: 1"))
                .when(fcmService).sendPushNotificationToUser(any(), any(), any());

        assertThrows(IllegalArgumentException.class, () -> consumer.listenDlt(MESSAGE, ack));
        verifyNoInteractions(ack);
    }
}
