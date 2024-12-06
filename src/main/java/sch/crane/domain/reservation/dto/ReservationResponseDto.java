package sch.crane.domain.reservation.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.reservation.entity.Reservation;
import sch.crane.domain.reservation.entity.enums.Instrument;
import sch.crane.domain.reservation.entity.enums.Status;
import sch.crane.domain.user.entity.User;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationResponseDto {

    private Long reservation_id;
    private LocalDateTime time;
    private Boolean possible;
    private Status status;
    private Instrument instrument;
    private User user;

    @Builder
    public ReservationResponseDto(Long reservation_id,
                                  LocalDateTime time,
                                  Boolean possible,
                                  Status status,
                                  Instrument instrument,
                                  User user) {
        this.reservation_id = reservation_id;
        this.time = time;
        this.possible = possible;
        this.status = status;
        this.instrument = instrument;
        this.user = user;
    }

    public static ReservationResponseDto from(Reservation reservation) {
        return ReservationResponseDto.builder()
                .reservation_id(reservation.getReservation_id())
                .time(reservation.getTime())
                .possible(reservation.getPossible())
                .status(reservation.getStatus())
                .instrument(reservation.getInstrument())
                .user(reservation.getUser())
                .build();
    }
}
