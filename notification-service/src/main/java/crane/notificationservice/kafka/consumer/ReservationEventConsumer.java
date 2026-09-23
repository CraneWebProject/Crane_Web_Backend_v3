package crane.notificationservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import crane.notificationservice.dto.ReservationEvent;
import crane.notificationservice.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationEventConsumer {

    private final FcmService fcmService;
    private final ObjectMapper objectMapper;


    // 예외는 컨테이너로 전파해 재시도 → DLT 경로를 타게 하고, 성공한 경우에만 ack 한다.
    @KafkaListener(
            topics = "reservation-events-topic",
            groupId = "notification-group",
            containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    public void listen(
            String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack
    ) throws JsonProcessingException, FirebaseMessagingException {
        log.debug("수신된 원본 메시지 - partition: {}, offset: {}, message: {}", partition, offset, message);
        handle(message);
        ack.acknowledge();
        log.info("푸시 발송 완료 및 offset commit = partition: {}, offset: {}, message: {} ", partition, offset, message);
    }

    // 메인 리스너의 재시도를 모두 소진해 DLT 로 넘어온 메시지를 재처리한다.
    @KafkaListener(
            topics = "reservation-events-topic.DLT",
            groupId = "notification-dlt-group",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void listenDlt(String message, Acknowledgment ack) throws JsonProcessingException, FirebaseMessagingException {
        handle(message);
        ack.acknowledge();
        log.info("DLT 재처리 완료 - message: {}", message);
    }

    private void handle(String message) throws JsonProcessingException, FirebaseMessagingException {
        ReservationEvent reservationEvent = parseAndValidateEvent(message);
        fcmService.sendPushNotificationToUser(reservationEvent.getUserId(), reservationEvent.getReservationStatus(), reservationEvent.getNotificationMessage());
    }


    private ReservationEvent parseAndValidateEvent(String message) throws JsonProcessingException {
        return objectMapper.readValue(message, ReservationEvent.class);
    }

}
