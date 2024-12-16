package crane.reservationservice.dto;

import crane.reservationservice.entity.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationRequestDto {

    private Long reservationId;
    private LocalDateTime time;
    private Long instrumentId;
    private Status status;

    @Builder
    public ReservationRequestDto(Long reservationId,
                                 LocalDateTime time,
                                 Long instrumentId,
                                 Status status) {
        this.reservationId = reservationId;
        this.time = time;
        this.instrumentId = instrumentId;
        this.status = status;
    }
}
