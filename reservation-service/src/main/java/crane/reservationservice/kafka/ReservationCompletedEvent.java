package crane.reservationservice.kafka;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationCompletedEvent {
    private Long userId;
    private Long reservationId;
    private String time;
    private Long instrumentId;
    private String eventType;
    private String notificationMessage;
    private String reservationStatus;

    @Builder
    public ReservationCompletedEvent(Long userId, Long reservationId, String time, Long instrumentId, String eventType, String notificationMessage, String reservationStatus) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.time = time;
        this.instrumentId = instrumentId;
        this.eventType = eventType;
        this.notificationMessage = notificationMessage;
        this.reservationStatus = reservationStatus;
    }
}
