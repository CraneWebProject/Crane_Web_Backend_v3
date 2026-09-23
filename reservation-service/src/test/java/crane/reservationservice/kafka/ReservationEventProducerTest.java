package crane.reservationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 같은 사용자의 이벤트가 같은 파티션으로 가도록 userId 를 키로 발행하는지 확인한다.
@ExtendWith(MockitoExtension.class)
class ReservationEventProducerTest {

    @Mock
    KafkaTemplate<String, String> reservationKafkaTemplate;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    ReservationEventProducer producer;

    @Test
    void sendsWithUserIdAsKey() throws Exception {
        ReflectionTestUtils.setField(producer, "topicName", "reservation-events-topic");
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(reservationKafkaTemplate.send(eq("reservation-events-topic"), any(String.class), any(String.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        producer.sendReservationSuccessEvent(1L, 2L, LocalDateTime.now(), 42L);
        producer.sendReservationCancelEvent(1L, 2L, LocalDateTime.now(), 42L);

        verify(reservationKafkaTemplate, times(2))
                .send(eq("reservation-events-topic"), eq("42"), any(String.class)); // 키 = userId
    }
}
