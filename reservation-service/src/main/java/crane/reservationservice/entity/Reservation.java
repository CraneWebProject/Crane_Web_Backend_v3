package crane.reservationservice.entity;

import crane.reservationservice.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private Long userId;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private Boolean possible;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @JoinColumn(name = "instrumentId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Instrument instrument;


    public void updateReservation(LocalDateTime time,
                                  boolean possible,
                                  Instrument instrument,
                                  Long userId){
        this.time = time;
        this.possible = possible;
        this.instrument = instrument;
        this.userId = userId;
    }

    @Builder
    public Reservation(Long userId,
                       LocalDateTime time,
                       Boolean possible,
                       Status status,
                       Instrument instrument) {
        this.userId = userId;
        this.time = time;
        this.possible = possible;
        this.status = status;
        this.instrument = instrument;
    }
}
