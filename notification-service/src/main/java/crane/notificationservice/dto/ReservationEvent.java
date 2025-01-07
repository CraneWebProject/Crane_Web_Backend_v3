package crane.notificationservice.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class ReservationEvent {
    private Long userId;
    private Long reservationId;
    private String time;
    private Long instrumentId;
    private String eventType;
    private String notificationMessage;
    private String reservationStatus;

    @Builder
    public ReservationEvent(Long userId, Long reservationId, String time, Long instrumentId, String eventType, String notificationMessage, String reservationStatus) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.time = time;
        this.instrumentId = instrumentId;
        this.eventType = eventType;
        this.notificationMessage = notificationMessage;
        this.reservationStatus = reservationStatus;
    }
}
