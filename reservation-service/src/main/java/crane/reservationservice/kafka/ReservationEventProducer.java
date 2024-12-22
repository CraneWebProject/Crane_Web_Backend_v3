package crane.reservationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationEventProducer {

    private final KafkaTemplate<String, String> reservationKafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.reservation-events}")
    private String topicName;

    private void sendEvent(ReservationCompletedEvent reservationCompletedEvent) {
        try{
            String value = objectMapper.writeValueAsString(reservationCompletedEvent);
            reservationKafkaTemplate.send(topicName, value)
                    .whenComplete((result, exception) -> {
                        if (exception == null) {
                            log.info("예약 정보 전송성공: {}, 오프셋: {}",
                                    reservationCompletedEvent, result.getRecordMetadata().offset());
                        }else{
                            log.error("예약 정보 전송 실패: {}", reservationCompletedEvent, exception );
                        }
                    });
        }catch(Exception e){
            log.error("JSON 변환 실패: {}", reservationCompletedEvent, e);
        }
    }

    public void sendReservationSuccessEvent(Long reservationId, Long instrumentId, LocalDateTime time , Long userId ) {
        ReservationCompletedEvent reservationCompletedEvent = ReservationCompletedEvent.builder()
                .reservationId(reservationId)
                .instrumentId(instrumentId)
                .time(time.toString())
                .userId(userId)
                .eventType("RESERVATION_SUCCESS")
                .reservationStatus("SUCCESS")
                .notificationMessage("예약이 완료되었습니다.")
                .build();

        sendEvent(reservationCompletedEvent);
    }

    public void sendReservationCancelEvent(Long reservationId, Long instrumentId, LocalDateTime time , Long userId ) {
        ReservationCompletedEvent reservationCompletedEvent = ReservationCompletedEvent.builder()
                .reservationId(reservationId)
                .instrumentId(instrumentId)
                .time(time.toString())
                .userId(userId)
                .eventType("RESERVATION_CANCEL")
                .reservationStatus("CANCELED")
                .notificationMessage("예약이 취소되었습니다.")
                .build();

        sendEvent(reservationCompletedEvent);
    }

//    public void sendMessage(String topic, String message) {
//        kafkaTemplate.send(topic, message); // Kafka로 메시지 전송
//        System.out.println("Produced message: " + message);
//    }
}
