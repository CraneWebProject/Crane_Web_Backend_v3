package crane.notificationservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    @KafkaListener(
            topics = "reservation-events-topic",
            groupId = "notification-group"
    )
    public void listen(
            String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack
    ){
        log.debug("수신된 원본 메시지 - partition: {}, offset: {}, message: {}", partition, offset, message);
        try {
            ReservationEvent reservationEvent = parseAndValidateEvent(message);

            fcmService.sendPushNotificationToUser(reservationEvent.getUserId(), reservationEvent.getReservationStatus(), reservationEvent.getNotificationMessage());
            ack.acknowledge();
            log.info("푸시 발송 완료 및 offset commit = partition: {}, offset: {}, message: {} ", partition, offset, message);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류: " + e.getMessage()); // 오류 메시지 출력
        }
    }


    private ReservationEvent parseAndValidateEvent(String message) throws JsonProcessingException {
        return objectMapper.readValue(message, ReservationEvent.class);
    }

}
