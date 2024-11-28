package sch.crane.domain.reservation.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.reservation.entity.enums.Instrument;
import sch.crane.domain.reservation.entity.enums.Status;
import sch.crane.domain.user.entity.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;

    private LocalDateTime time;

    private Boolean possible;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Instrument instrument;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void updateReservation(LocalDateTime time,
                                  boolean possible,
                                  Instrument instrument,
                                  User user) {
        this.time = time;
        this.possible = possible;
        this.instrument = instrument;
        this.user = user;
    }

    @Builder
    public Reservation(LocalDateTime time,
                       Boolean possible,
                       Status status,
                       Instrument instrument,
                       User user) {
        this.time = time;
        this.possible = possible;
        this.status = status;
        this.instrument = instrument;
        this.user = user;
    }



}
