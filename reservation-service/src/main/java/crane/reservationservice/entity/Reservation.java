package crane.reservationservice.entity;

import crane.reservationservice.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private Boolean possible;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @JoinColumn(name = "instrument_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Instrument instrument;


}
