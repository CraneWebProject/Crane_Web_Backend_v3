package sch.crane.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.reservation.entity.enums.Instrument;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationRequestDto {

        private Long reservation_id;

        private LocalDateTime time;

        private Instrument instrument;

        @Builder
        public ReservationRequestDto(Long reservation_id, LocalDateTime time, Instrument instrument) {
                this.reservation_id = reservation_id;
                this.time = time;
                this.instrument = instrument;
        }
}
