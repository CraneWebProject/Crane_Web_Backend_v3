package crane.reservationservice.dto;

import crane.reservationservice.entity.Instrument;
import crane.reservationservice.entity.Reservation;
import crane.reservationservice.entity.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationResponseDto {
    private Long reservationId;
    private Long userId;
    private LocalDateTime time;
    private Boolean possible;
    private Status status;
    private Instrument instrument;

    @Builder
    public ReservationResponseDto(Long reservationId,
                                  Long userId,
                                  LocalDateTime time,
                                  Boolean possible,
                                  Status status,
                                  Instrument instrument) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.time = time;
        this.possible = possible;
        this.status = status;
        this.instrument = instrument;
    }

    public static ReservationResponseDto from(Reservation reservation) {
        return ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUserId())
                .time(reservation.getTime())
                .possible(reservation.getPossible())
                .status(reservation.getStatus())
                .instrument(reservation.getInstrument())
                .build();
    }
}
